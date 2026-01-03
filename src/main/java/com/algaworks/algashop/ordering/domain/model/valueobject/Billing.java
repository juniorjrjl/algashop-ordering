package com.algaworks.algashop.ordering.domain.model.valueobject;

import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record Billing(FullName fullName,
                      Document document,
                      Phone phone,
                      Address address,
                      Email email) {

    public Billing {
        requireNonNull(fullName);
        requireNonNull(document);
        requireNonNull(phone);
        requireNonNull(address);
        requireNonNull(email);
    }

}
