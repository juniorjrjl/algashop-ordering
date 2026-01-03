package com.algaworks.algashop.ordering.domain.model.valueobject;

import org.jspecify.annotations.NonNull;

import static com.algaworks.algashop.ordering.domain.model.utility.validator.FieldValidations.requiresNonBlank;

public record Phone(String value) {

    public static final Phone ANONYMOUS = new Phone("000-000-0000");

    public Phone {
        requiresNonBlank(value);
    }

    @Override
    @NonNull
    public String toString() {
        return value();
    }
}
