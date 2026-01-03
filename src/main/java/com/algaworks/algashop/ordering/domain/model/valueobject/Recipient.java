package com.algaworks.algashop.ordering.domain.model.valueobject;

import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record Recipient(
        FullName fullName,
        Document document,
        Phone phone
) {

    public Recipient {
        requireNonNull(fullName);
        requireNonNull(document);
        requireNonNull(phone);
    }

}
