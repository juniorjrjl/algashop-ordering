package com.algaworks.algashop.ordering.presentation;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException() {

    }

    public BadGatewayException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
