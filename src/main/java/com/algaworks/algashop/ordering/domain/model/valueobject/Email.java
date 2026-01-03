package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.validator.FieldValidations;
import org.jspecify.annotations.NonNull;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessage.VALIDATION_ERROR_EMAIL_IS_INVALID;
import static java.util.Objects.requireNonNull;

public record Email(String value) {

    public static final Email ANONYMOUS = new Email("anonymous@anonymous.com");

    public Email {
        FieldValidations.validateEmail(requireNonNull(value), VALIDATION_ERROR_EMAIL_IS_INVALID);
    }


    @Override
    @NonNull
    public String toString() {
        return value();
    }
}
