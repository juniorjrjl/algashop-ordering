package com.algaworks.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record Phone(String value) {

    public static final Phone ANONYMOUS = new Phone("000-000-0000");

    public Phone {
        if (requireNonNull(value).isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    @NonNull
    public String toString() {
        return value();
    }
}
