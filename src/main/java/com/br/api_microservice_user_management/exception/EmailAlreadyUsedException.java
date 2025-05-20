package com.br.api_microservice_user_management.exception;

/**
 * Exceção lançada quando o e-mail já está em uso por outro usuário.
 */
public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}

