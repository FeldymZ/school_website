package com.school.api.formation.continues.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Renvoie le vrai message (« Désolé, cette session est complète »…) au lieu de
 * « Une erreur interne est survenue », avec un code 400 au lieu de 500.
 * Passe avant le GlobalExceptionHandler grâce à @Order.
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SessionFormationExceptionHandler {

    @ExceptionHandler(SessionFormationException.class)
    public ResponseEntity<Map<String, Object>> handle(SessionFormationException e) {
        log.info("ℹ Session de formation : {}", e.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", e.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(body);
    }
}