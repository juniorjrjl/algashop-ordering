package com.algaworks.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record Document(String value) {

    public static final Document ANONYMOUS = new Document("000-00-0000");

    public Document{
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
