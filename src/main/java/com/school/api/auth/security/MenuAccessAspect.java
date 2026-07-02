package com.school.api.auth.security;

import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MenuAccessAspect {

    private final UserRepository userRepository;

    @Before("@annotation(requiresMenuAccess)")
    public void checkMenuAccess(JoinPoint joinPoint, RequiresMenuAccess requiresMenuAccess) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentification requise");
        }

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Utilisateur introuvable"));

        // SUPERADMIN : accès total, toujours
        if (user.getRole() == Role.SUPERADMIN) {
            return;
        }

        String requiredKey = requiresMenuAccess.value();

        if (user.getMenuAccess() == null || !user.getMenuAccess().contains(requiredKey)) {
            throw new AccessDeniedException(
                    "Accès refusé : permission '" + requiredKey + "' requise"
            );
        }
    }
}