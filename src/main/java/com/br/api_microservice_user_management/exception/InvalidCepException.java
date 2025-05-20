package com.br.api_microservice_user_management.exception;

/**
 * Exceção lançada quando o CEP informado é inválido ou não encontrado.
 */
public class InvalidCepException extends RuntimeException {
    public InvalidCepException(String message) {
        super(message);
    }
}

