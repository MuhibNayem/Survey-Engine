package com.bracits.surveyengine.common.audit.aspect;

import com.bracits.surveyengine.admin.context.TenantContext;
import com.bracits.surveyengine.common.audit.AuditLogService;
import com.bracits.surveyengine.common.audit.annotation.Auditable;
import com.bracits.surveyengine.common.audit.context.AuditContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

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

    @Around("execution(* com.bracits.surveyengine..service..*(..)) && !within(com.bracits.surveyengine..*Filter*)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Auditable auditable = resolveAuditableAnnotation(joinPoint);
        if (auditable == null) {
            return joinPoint.proceed();
        }

        String action = auditable.action();
        String actor = resolveActor();
        String tenantId = resolveTenantId();
        String ipAddress = resolveClientIp();
        String impersonatedBy = TenantContext.getImpersonatedBy();

        // 1. Setup thread-local context for JPA EntityListeners
        AuditContext context = AuditContext.builder()
                .action(action)
                .actor(actor)
                .tenantId(tenantId)
                .ipAddress(ipAddress)
                .impersonatedBy(impersonatedBy)
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
                    String reason = "Action: " + action;
                    if (impersonatedBy != null) {
                        reason += " [impersonated by " + impersonatedBy + "]";
                    }
                    auditLogService.record(
                            tenantId,
                            "System",
                            joinPoint.getSignature().getName(),
                            action,
                            actor,
                            reason,
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

    private Auditable resolveAuditableAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method interfaceMethod = signature.getMethod();
        Class<?> targetClass = joinPoint.getTarget() != null
                ? AopProxyUtils.ultimateTargetClass(joinPoint.getTarget())
                : signature.getDeclaringType();
        Method targetMethod = AopUtils.getMostSpecificMethod(interfaceMethod, targetClass);
        Auditable fromTargetMethod = AnnotationUtils.findAnnotation(targetMethod, Auditable.class);
        if (fromTargetMethod != null) {
            return fromTargetMethod;
        }
        return AnnotationUtils.findAnnotation(interfaceMethod, Auditable.class);
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
