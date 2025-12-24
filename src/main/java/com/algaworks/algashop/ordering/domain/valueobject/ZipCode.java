package com.algaworks.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record ZipCode(String value) {

    public ZipCode{
        if (requireNonNull(value).isBlank() || value.length() != 5) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    @NonNull
    public String toString() {
        return value();
    }
}
