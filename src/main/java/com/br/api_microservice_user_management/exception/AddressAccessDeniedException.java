package com.br.api_microservice_user_management.exception;

/**
 * Exceção lançada quando o usuário não tem permissão para acessar ou modificar o endereço.
 */
public class AddressAccessDeniedException extends RuntimeException {
    public AddressAccessDeniedException(String message) {
        super(message);
    }
}

