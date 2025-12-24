package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.validator.FieldValidations;
import lombok.Builder;

import static java.util.Objects.requireNonNull;

public record Address(
        String street,
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
