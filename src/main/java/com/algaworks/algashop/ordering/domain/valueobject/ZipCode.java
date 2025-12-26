package com.algaworks.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import static com.algaworks.algashop.ordering.domain.utility.validator.FieldValidations.requiresNonBlank;

public record ZipCode(String value) {

    public ZipCode{
        requiresNonBlank(value);
        if (value.length() != 5) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    @NonNull
    public String toString() {
        return value();
    }
}
