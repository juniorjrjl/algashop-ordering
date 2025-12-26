package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record ShippingInfo(FullName fullName,
                           Document document,
                           Phone phone,
                           Address address) {

    public ShippingInfo {
        requireNonNull(fullName);
        requireNonNull(document);
        requireNonNull(phone);
        requireNonNull(address);
    }

}
