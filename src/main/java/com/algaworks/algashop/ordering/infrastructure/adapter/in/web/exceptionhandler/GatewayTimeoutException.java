package com.algaworks.algashop.ordering.infrastructure.adapter.in.web.exceptionhandler;

public class GatewayTimeoutException extends RuntimeException {

    public GatewayTimeoutException() {

    }

    public GatewayTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
