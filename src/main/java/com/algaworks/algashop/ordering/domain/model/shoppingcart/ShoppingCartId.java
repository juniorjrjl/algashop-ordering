package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.IdGenerator;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record ShoppingCartId(UUID value) {

    public ShoppingCartId(){
        this(IdGenerator.generateUUID());
    }

    public ShoppingCartId(final UUID value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
