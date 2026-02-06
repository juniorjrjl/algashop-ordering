package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
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
