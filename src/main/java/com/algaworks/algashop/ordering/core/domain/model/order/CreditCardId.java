package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.IdGenerator;

import java.util.UUID;

public record CreditCardId(UUID value) {

    public CreditCardId() {
        this(IdGenerator.generateUUID());
    }

}
