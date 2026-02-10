package com.algaworks.algashop.ordering.domain.model.customer;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST;
import static java.util.Objects.requireNonNull;

public record BirthDate(LocalDate value) {

    public BirthDate {
        if (requireNonNull(value).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
        }
    }

    public int age(){
        return Period.between(value(), LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return value.format(DateTimeFormatter.ISO_DATE);
    }
}
