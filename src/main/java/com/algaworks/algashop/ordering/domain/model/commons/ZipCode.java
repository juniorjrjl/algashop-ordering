package com.algaworks.algashop.ordering.domain.model.commons;

import static com.algaworks.algashop.ordering.domain.model.FieldValidations.requiresNonBlank;

public record ZipCode(String value) {

    public ZipCode{
        requiresNonBlank(value);
        if (value.length() != 5) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return value();
    }
}
