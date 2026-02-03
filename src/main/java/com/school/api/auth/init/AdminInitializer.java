package com.school.api.auth.init;

import com.school.api.auth.entity.Role;
import com.school.api.auth.entity.User;
import com.school.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {

            String email = "noreply@esiitech-gabon.com";

            if (userRepository.findByEmail(email).isEmpty()) {

                userRepository.save(
                    User.builder()
                        .email(email)
                        .password(passwordEncoder.encode("Esiitech2026#"))
                        .role(Role.SUPERADMIN)   // ENUM
                        .enabled(true)
                        .build()
                );

                System.out.println("✅ Superadmin créé : " + email);
            }
        };
    }
}
