package com.algaworks.algashop.ordering.domain.model.utility.validator;

import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;


@NoArgsConstructor(access = PRIVATE)
public class FieldValidations {

    public static void validateEmail(final String email, final String message) {
        if (emailIsInValid(email)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean emailIsInValid(final String email) {
        return (isNull(email) || email.isBlank() || !EmailValidator.getInstance().isValid(email));
    }

    public static void requiresNonBlank(final String value) {
        if (requireNonNull(value).isBlank()) {
            throw new IllegalArgumentException();
        }
    }

}
