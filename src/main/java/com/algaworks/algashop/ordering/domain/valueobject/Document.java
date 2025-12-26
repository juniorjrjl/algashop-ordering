package com.algaworks.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import static com.algaworks.algashop.ordering.domain.utility.validator.FieldValidations.requiresNonBlank;

public record Document(String value) {

    public static final Document ANONYMOUS = new Document("000-00-0000");

    public Document{
        requiresNonBlank(value);
    }

    @Override
    @NonNull
    public String toString() {
        return value();
    }
}
