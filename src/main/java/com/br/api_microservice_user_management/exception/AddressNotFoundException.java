package com.br.api_microservice_user_management.exception;

/**
 * Exceção lançada quando o endereço não é encontrado.
 */
public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String message) {
        super(message);
    }
}

