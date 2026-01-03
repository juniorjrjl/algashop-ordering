package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;
import org.jspecify.annotations.NonNull;

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
    @NonNull
    public String toString() {
        return value.toString();
    }
}
