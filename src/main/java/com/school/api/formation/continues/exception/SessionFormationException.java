package com.school.api.formation.continues.exception;

/**
 * Erreur « normale » des sessions de formation (session complète, champ manquant…).
 * Son message est affiché tel quel à l'utilisateur, avec un code 400.
 */
public class SessionFormationException extends RuntimeException {
    public SessionFormationException(String message) {
        super(message);
    }
}