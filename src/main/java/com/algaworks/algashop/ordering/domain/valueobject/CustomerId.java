package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.IdGenerator;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record CustomerId(UUID value) {

    public CustomerId(){
        this(IdGenerator.generateUUID());
    }

    public CustomerId(final UUID value) {
        this.value = requireNonNull(value);
    }

    @Override
    @NonNull
    public String toString() {
        return value.toString();
    }
}
