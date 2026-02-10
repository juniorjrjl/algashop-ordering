package com.algaworks.algashop.ordering.domain.model.commons;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.VALIDATION_ERROR_FULL_NAME_IS_NULL_OR_BLANK;
import static com.algaworks.algashop.ordering.domain.model.FieldValidations.requiresNonBlank;

public record FullName(String firstName, String lastName) {

    public static final FullName ANONYMOUS = new FullName("Anonymous", "Anonymous");

    public FullName(final String firstName, final String lastName) {
        requiresNonBlank(firstName, VALIDATION_ERROR_FULL_NAME_IS_NULL_OR_BLANK);
        requiresNonBlank(lastName, VALIDATION_ERROR_FULL_NAME_IS_NULL_OR_BLANK);

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
