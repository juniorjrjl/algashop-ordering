package com.algaworks.algashop.ordering.domain.model.exception;

public class DomainException extends RuntimeException{

    public DomainException() {}

    public DomainException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DomainException(final String message) {
        super(message);
    }
}
