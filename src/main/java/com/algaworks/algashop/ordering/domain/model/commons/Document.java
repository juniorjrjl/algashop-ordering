package com.algaworks.algashop.ordering.domain.model.commons;

import static com.algaworks.algashop.ordering.domain.model.FieldValidations.requiresNonBlank;

public record Document(String value) {

    public static final Document ANONYMOUS = new Document("000-00-0000");

    public Document{
        requiresNonBlank(value);
    }

    @Override
    public String toString() {
        return value();
    }
}
