package com.school.api.common.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
       🔴 NOT FOUND
       ========================= */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(
            ResourceNotFoundException ex
    ) {

        log.warn("❌ NOT FOUND : {}", ex.getMessage());

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                ex.getMessage()
        );
    }

    /* =========================
       🟠 BAD REQUEST
       ========================= */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(
            IllegalArgumentException ex
    ) {

        log.warn("⚠️ BAD REQUEST : {}", ex.getMessage());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                ex.getMessage()
        );
    }

    /* =========================
       📝 VALIDATION @VALID
       ========================= */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        log.warn("⚠️ VALIDATION ERROR");

        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {

            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        return ResponseEntity.badRequest().body(

                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "VALIDATION_ERROR",
                        "message", "Erreur de validation",
                        "errors", errors
                )
        );
    }

    /* =========================
       📝 VALIDATION PARAMETRES
       ========================= */

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(
            ConstraintViolationException ex
    ) {

        log.warn("⚠️ CONSTRAINT VIOLATION");

        Map<String, String> errors = new LinkedHashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {

            errors.put(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            );
        }

        return ResponseEntity.badRequest().body(

                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "VALIDATION_ERROR",
                        "message", "Erreur de validation",
                        "errors", errors
                )
        );
    }

    /* =========================
       📦 JSON / ENUM / DATE
       ========================= */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNotReadable(
            HttpMessageNotReadableException ex
    ) {

        log.warn("⚠️ INVALID REQUEST BODY", ex);

        return buildResponse(

                HttpStatus.BAD_REQUEST,

                "BAD_REQUEST",

                "Le contenu de la requête est invalide."
        );
    }

    /* =========================
       🔒 ACCESS DENIED
       ========================= */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(
            AccessDeniedException ex
    ) {

        log.warn("⛔ ACCESS DENIED : {}", ex.getMessage());

        return buildResponse(

                HttpStatus.FORBIDDEN,

                "FORBIDDEN",

                "Vous n'avez pas les droits nécessaires."
        );
    }

    /* =========================
       🔐 AUTHENTICATION
       ========================= */

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthentication(
            AuthenticationException ex
    ) {

        log.warn("🔐 AUTHENTICATION : {}", ex.getMessage());

        return buildResponse(

                HttpStatus.UNAUTHORIZED,

                "UNAUTHORIZED",

                "Authentification requise."
        );
    }

    /* =========================
       🔐 SECURITY
       ========================= */

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurity(
            SecurityException ex
    ) {

        log.warn("🔐 SECURITY : {}", ex.getMessage());

        return buildResponse(

                HttpStatus.UNAUTHORIZED,

                "UNAUTHORIZED",

                ex.getMessage()
        );
    }

    /* =========================
       🔥 ERREUR GENERIQUE
       ========================= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(
            Exception ex
    ) {

        log.error("🔥 INTERNAL ERROR", ex);

        return buildResponse(

                HttpStatus.INTERNAL_SERVER_ERROR,

                "INTERNAL_SERVER_ERROR",

                "Une erreur interne est survenue."
        );
    }

    /* =========================
       🧠 BUILDER
       ========================= */

    private ResponseEntity<Map<String, Object>> buildResponse(

            HttpStatus status,

            String error,

            String message
    ) {

        return ResponseEntity.status(status).body(

                Map.of(

                        "timestamp", LocalDateTime.now(),

                        "status", status.value(),

                        "error", error,

                        "message", message
                )
        );
    }
}