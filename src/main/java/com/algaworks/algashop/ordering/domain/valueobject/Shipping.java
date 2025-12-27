package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

@Builder(toBuilder = true)
public record Shipping(
        Money cost,
        LocalDate expectedDate,
        Recipient recipient,
        Address address
) {

    public Shipping {
        requireNonNull(cost);
        requireNonNull(expectedDate);
        requireNonNull(recipient);
        requireNonNull(address);
    }

}
