package com.algaworks.algashop.ordering.domain.model.commons;

import org.jspecify.annotations.NonNull;

import static com.algaworks.algashop.ordering.domain.model.FieldValidations.requiresNonBlank;

public record FullName(String firstName, String lastName) {

    public static final FullName ANONYMOUS = new FullName("Anonymous", "Anonymous");

    public FullName(final String firstName, final String lastName) {
        requiresNonBlank(firstName);
        requiresNonBlank(lastName);

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    @Override
    @NonNull
    public String toString() {
        return firstName + " " + lastName;
    }
}
