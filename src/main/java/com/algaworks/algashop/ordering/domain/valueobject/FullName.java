package com.algaworks.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.isNull;

public record FullName(String firstName, String lastName) {

    public static final FullName ANONYMOUS = new FullName("Anonymous", "Anonymous");

    public FullName(final String firstName, final String lastName) {
        if (isNull(firstName) || firstName.isBlank()){
            throw new IllegalArgumentException();
        }
        if (isNull(lastName) || lastName.isBlank()){
            throw new IllegalArgumentException();
        }

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    @Override
    @NonNull
    public String toString() {
        return firstName + " " + lastName;
    }
}
