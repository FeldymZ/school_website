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

        // ✅ Évaluation du target AVANT proceed()
        // → permet de récupérer le nom de l'entité même pour les suppressions
        //   (l'entité existe encore à ce moment)
        String preTarget = resolveTarget(auditLog.target(), joinPoint, null);

        try {
            Object result = joinPoint.proceed();

            String actor = resolveActor(joinPoint);

            // ✅ Tentative d'amélioration avec #result (valeur de retour)
            // → si la méthode retourne l'entité (ex: suppression qui retourne l'objet supprimé),
            //   on peut extraire un champ plus lisible
            String postTarget = resolveTarget(auditLog.target(), joinPoint, result);

            // On préfère le target le plus informatif
            String finalTarget = chooseBestTarget(preTarget, postTarget, auditLog.target());

            auditService.log(actor, auditLog.action(), finalTarget);
            return result;

        } catch (Throwable ex) {

            if (!auditLog.failureAction().isBlank()) {
                String actor = resolveActor(joinPoint);
                // On utilise le preTarget car l'entité peut ne plus exister
                auditService.log(actor, auditLog.failureAction(), preTarget + " | " + ex.getMessage());
            }

            throw ex;
        }
    }

    /* ================= CHOIX DU MEILLEUR TARGET ================= */

    /**
     * Préfère le target le plus informatif :
     * - Si postTarget est différent et non vide, on le prend (retour de méthode)
     * - Sinon, on garde le preTarget (évalué avant proceed)
     * - En dernier recours : la valeur brute de l'expression
     */
    private String chooseBestTarget(String pre, String post, String rawExpression) {
        if (post != null && !post.isBlank() && !post.equals(rawExpression) && !post.equals("-")) {
            return post;
        }
        if (pre != null && !pre.isBlank() && !pre.equals(rawExpression) && !pre.equals("-")) {
            return pre;
        }
        return pre != null ? pre : "-";
    }

    /* ================= RÉSOLUTION DE L'ACTEUR ================= */

    private String resolveActor(ProceedingJoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof LoginRequest req) return req.email();
        }
        return "ANONYME";
    }

    /* ================= RÉSOLUTION DU TARGET (SpEL) ================= */

    /**
     * @param expression  Expression SpEL (ex: "#request.email", "#result.titre")
     * @param joinPoint   Point de jonction AOP
     * @param result      Valeur de retour de la méthode (null si évaluation pré-proceed)
     */
    private String resolveTarget(String expression, ProceedingJoinPoint joinPoint, Object result) {
        if (expression == null || expression.isBlank()) return "-";

        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String[] paramNames = nameDiscoverer.getParameterNames(method);
            Object[] args = joinPoint.getArgs();

            StandardEvaluationContext context = new StandardEvaluationContext();

            // Bind des paramètres de méthode
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }

            // ✅ Bind de #result si disponible (pour target = "#result.titre" par exemple)
            if (result != null) {
                context.setVariable("result", result);
            }

            Object value = parser.parseExpression(expression).getValue(context);
            return value != null ? value.toString() : "-";

        } catch (Exception e) {
            // Fallback : retourne l'expression brute
            return expression;
        }
    }
}