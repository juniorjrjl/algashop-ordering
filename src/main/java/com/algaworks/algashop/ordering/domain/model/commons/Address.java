package com.algaworks.algashop.ordering.domain.model.commons;

import com.algaworks.algashop.ordering.domain.model.FieldValidations;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public record Address(
        String street,
        @Nullable
        String complement,
        String neighborhood,
        String number,
        String city,
        String state,
        ZipCode zipCode) {

    @Builder(toBuilder = true)
    public Address{
        FieldValidations.requiresNonBlank(street);
        FieldValidations.requiresNonBlank(neighborhood);
        FieldValidations.requiresNonBlank(number);
        FieldValidations.requiresNonBlank(city);
        FieldValidations.requiresNonBlank(state);
        requireNonNull(zipCode);
    }

}
