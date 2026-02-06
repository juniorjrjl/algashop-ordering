package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.model.IdGenerator;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record ProductId(UUID value) {

    public ProductId(){
        this(IdGenerator.generateUUID());
    }

    public ProductId(final UUID value) {
        this.value = requireNonNull(value);
    }

    @Override
    @NonNull
    public String toString() {
        return value.toString();
    }

}
