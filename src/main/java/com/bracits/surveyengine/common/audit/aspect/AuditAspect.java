package com.bracits.surveyengine.common.audit.aspect;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.audit.AuditLogService;
import com.bracits.surveyengine.common.audit.annotation.Auditable;
import com.bracits.surveyengine.common.audit.context.AuditContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Spring AOP Aspect that intercepts methods annotated with {@link Auditable}.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Populates {@link AuditContext} (ThreadLocal) so downstream Hibernate
 *       {@link com.bracits.surveyengine.common.audit.listener.HibernateAuditListener}
 *       can write rich, business-intent-aware audit log entries when entities are persisted.</li>
 *   <li>Acts as a <strong>fallback writer</strong> for non-entity actions
 *       (e.g. LOGIN, LOGOUT, REGISTER) that do not trigger any JPA lifecycle callback.</li>
 * </ol>
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String action = auditable.action();
        String actor = resolveActor();
        String tenantId = resolveTenantId();
        String ipAddress = resolveClientIp();

        // 1. Setup thread-local context for JPA EntityListeners
        AuditContext context = AuditContext.builder()
                .action(action)
                .actor(actor)
                .tenantId(tenantId)
                .ipAddress(ipAddress)
                .build();
        AuditContext.setContext(context);
        boolean clearInFinally = true;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            clearInFinally = false;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    AuditContext.clearContext();
                }
            });
        }

        boolean executedSuccessfully = false;
        try {
            Object result = joinPoint.proceed();
            executedSuccessfully = true;
            return result;
        } finally {
            if (clearInFinally) {
                AuditContext.clearContext();
            }

            // Fallback: for non-entity actions (no JPA save → no EntityListener fires)
            if (executedSuccessfully && isNonEntityAction(action)) {
                try {
                    auditLogService.record(
                            tenantId,
                            "System",
                            joinPoint.getSignature().getName(),
                            action,
                            actor,
                            "Action: " + action,
                            null, null,
                            ipAddress);
                } catch (Exception e) {
                    log.error("Fallback audit write failed for action={}", action, e);
                }
            }
        }
    }

    /**
     * Actions that are NOT associated with saving/updating a JPA entity.
     * The EntityListener will NOT fire for these, so the Aspect writes the audit row instead.
     */
    private boolean isNonEntityAction(String action) {
        return action.contains("LOGIN")
                || action.contains("LOGOUT")
                || action.contains("REGISTER")
                || action.contains("IMPERSONAT")
                || action.contains("REVERT");
    }

    private String resolveActor() {
        TenantContext.TenantInfo info = TenantContext.get();
        if (info != null && info.email() != null) {
            return info.email();
        }
        if (info != null && info.userId() != null) {
            return info.userId();
        }
        return "SYSTEM";
    }

    private String resolveTenantId() {
        return TenantContext.getTenantId();
    }

    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xff = request.getHeader("X-Forwarded-For");
                if (xff != null && !xff.isEmpty()) {
                    return xff.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.warn("Could not resolve client IP address");
        }
        return "UNKNOWN";
    }
}
