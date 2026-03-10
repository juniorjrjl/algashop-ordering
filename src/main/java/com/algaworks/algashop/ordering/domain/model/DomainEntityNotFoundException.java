package com.algaworks.algashop.ordering.domain.model;

public class DomainEntityNotFoundException extends RuntimeException{

    public DomainEntityNotFoundException() {}

    public DomainEntityNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DomainEntityNotFoundException(final String message) {
        super(message);
    }
}
