package com.algaworks.algashop.ordering.presentation;

public class UnprocessableEntityException extends RuntimeException {

    public UnprocessableEntityException() {
    }

    public UnprocessableEntityException(final String message) {
        super(message);
    }

    public UnprocessableEntityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
