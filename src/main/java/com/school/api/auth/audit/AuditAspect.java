package com.school.api.auth.audit;

import com.school.api.auth.dto.LoginRequest;
import com.school.api.auth.service.AdminAuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AdminAuditService auditService;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(auditLog)")
    public Object audit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {

        try {
            Object result = joinPoint.proceed();

            String actor  = resolveActor(joinPoint);
            String target = resolveTarget(auditLog.target(), joinPoint);
            auditService.log(actor, auditLog.action(), target);

            return result;

        } catch (Throwable ex) {

            if (!auditLog.failureAction().isBlank()) {
                String actor  = resolveActor(joinPoint);
                String target = resolveTarget(auditLog.target(), joinPoint);
                auditService.log(actor, auditLog.failureAction(), target + " | " + ex.getMessage());
            }

            throw ex;
        }
    }

    private String resolveActor(ProceedingJoinPoint joinPoint) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof LoginRequest req) return req.email();
        }

        return "ANONYME";
    }

    private String resolveTarget(String expression, ProceedingJoinPoint joinPoint) {

        if (expression == null || expression.isBlank()) return "-";

        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String[] paramNames = nameDiscoverer.getParameterNames(method);
            Object[] args = joinPoint.getArgs();

            StandardEvaluationContext context = new StandardEvaluationContext();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }

            Object value = parser.parseExpression(expression).getValue(context);
            return value != null ? value.toString() : "-";

        } catch (Exception e) {
            return expression;
        }
    }
}