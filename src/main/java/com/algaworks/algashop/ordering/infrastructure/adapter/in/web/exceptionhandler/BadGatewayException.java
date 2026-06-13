package com.algaworks.algashop.ordering.infrastructure.adapter.in.web.exceptionhandler;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException() {

    }

    public BadGatewayException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
