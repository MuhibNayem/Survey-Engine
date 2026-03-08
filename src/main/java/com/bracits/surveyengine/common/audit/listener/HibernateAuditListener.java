package com.bracits.surveyengine.common.audit.listener;

import com.bracits.surveyengine.common.audit.AuditLog;
import com.bracits.surveyengine.common.audit.AuditLogService;
import com.bracits.surveyengine.common.audit.context.AuditContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Replaces standard JPA @EntityListener to get true before/after states.
 * Standard JPA @PostUpdate only has access to the *new* entity state.
 * Hibernate's EventListener API provides both the old and new property states.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HibernateAuditListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private final EntityManagerFactory entityManagerFactory;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void registerListeners() {
        try {
            SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
            EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
            
            registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(this);
            registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(this);
            registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(this);
            
            log.info("Registered HibernateAuditListener for insert, update, delete events");
        } catch (Exception e) {
            log.warn("Failed to register Hibernate audit listener - automatic ORM auditing may not work", e);
        }
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof AuditLog) return;
        AuditContext context = AuditContext.getContext();
        // Skip auditing if not triggered via an @Auditable business action
        if (context == null) return;

        Map<String, Object> state = buildStateMap(event.getPersister().getPropertyNames(), event.getState());
        recordAudit(context, "CREATED", event.getEntity().getClass().getSimpleName(), event.getId().toString(), null, state);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof AuditLog) return;
        AuditContext context = AuditContext.getContext();
        if (context == null) return;

        Map<String, Object> oldState = buildStateMap(event.getPersister().getPropertyNames(), event.getOldState());
        Map<String, Object> newState = buildStateMap(event.getPersister().getPropertyNames(), event.getState());
        
        recordAudit(context, "UPDATED", event.getEntity().getClass().getSimpleName(), event.getId().toString(), oldState, newState);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (event.getEntity() instanceof AuditLog) return;
        AuditContext context = AuditContext.getContext();
        if (context == null) return;

        Map<String, Object> state = buildStateMap(event.getPersister().getPropertyNames(), event.getDeletedState());
        recordAudit(context, "DELETED", event.getEntity().getClass().getSimpleName(), event.getId().toString(), state, null);
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }

    private Map<String, Object> buildStateMap(String[] propertyNames, Object[] state) {
        if (propertyNames == null || state == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        // Skip uninitialized or problematic properties if needed, but standard types work fine
        for (int i = 0; i < propertyNames.length; i++) {
            // We only store simple representations to prevent deep cyclic JSON serialization issues
            Object value = state[i];
            map.put(propertyNames[i], simplifyValue(value));
        }
        return map;
    }

    private Object simplifyValue(Object value) {
        if (value == null) return null;
        // If it's another entity (like a many-to-one relationship), just grab its ID to avoid huge nested JSONs
        if (value.getClass().isAnnotationPresent(jakarta.persistence.Entity.class)) {
            try {
                var method = value.getClass().getMethod("getId");
                return method.invoke(value);
            } catch (Exception e) {
                return "EntityRef(" + value.getClass().getSimpleName() + ")";
            }
        }
        // Collections might cause lazy load issues or massive JSONs
        if (value instanceof java.util.Collection) {
            return "Collection(" + ((java.util.Collection<?>) value).size() + " items)";
        }
        return value;
    }

    private void recordAudit(AuditContext context, String dbAction, String entityType, String entityId, Map<String, Object> beforeState, Map<String, Object> afterState) {
        try {
            String beforeJson = toJson(beforeState);
            String afterJson = toJson(afterState);
            String tenantId = context.getTenantId();
            String action = context.getAction();
            String actor = context.getActor();
            String reason = dbAction + " via AOP";
            String ipAddress = context.getIpAddress();

            Runnable writer = () -> {
                try {
                    auditLogService.record(
                            tenantId,
                            entityType,
                            entityId,
                            action,
                            actor,
                            reason,
                            beforeJson,
                            afterJson,
                            ipAddress);
                } catch (Exception ex) {
                    log.error("Audit row write failed action={} entityType={} entityId={} tenant={}",
                            action, entityType, entityId, tenantId, ex);
                }
            };

            // Write immediately in an isolated REQUIRES_NEW transaction.
            // This avoids relying on afterCommit callbacks where tx resources may still be in completion phase.
            writer.run();
        } catch (Exception e) {
            log.error("Failed to record hibernate audit log for {} {}", entityType, entityId, e);
        }
    }

    private String toJson(Map<String, Object> state) {
        if (state == null) return null;
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize hibernate state for audit", e);
            return "{\"error\": \"Unserializable state\"}";
        }
    }
}
