package com.algaworks.algashop.ordering.core.domain.model.commons;

import com.algaworks.algashop.ordering.core.domain.model.FieldValidations;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessage.VALIDATION_ERROR_EMAIL_IS_INVALID;
import static java.util.Objects.requireNonNull;

public record Email(String value) {

    public static final Email ANONYMOUS = new Email("anonymous@anonymous.com");

    public Email {
        FieldValidations.validateEmail(requireNonNull(value), VALIDATION_ERROR_EMAIL_IS_INVALID);
    }


    @Override
    public String toString() {
        return value();
    }
}
