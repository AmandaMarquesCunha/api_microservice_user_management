package com.br.api_microservice_user_management.exception;

/**
 * Exceção lançada quando o usuário não é encontrado.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

