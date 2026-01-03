package com.algaworks.algashop.ordering.domain.model.valueobject;

import org.jspecify.annotations.NonNull;

import static com.algaworks.algashop.ordering.domain.model.utility.validator.FieldValidations.requiresNonBlank;

public record ProductName(String value) {

    public ProductName(final String value) {
        requiresNonBlank(value);
        this.value = value.trim();
    }

    @Override
    @NonNull
    public String toString() {
        return value;
    }
}
