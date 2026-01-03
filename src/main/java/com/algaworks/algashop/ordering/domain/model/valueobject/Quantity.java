package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.RichComparable;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

public record Quantity(Integer value) implements RichComparable<Quantity> {

    public static final Quantity ZERO = new Quantity(0);

    public Quantity{
        if (requireNonNull(value) < 0){
            throw new IllegalArgumentException();
        }
    }

    public Quantity add(final Quantity toAdd){
        return new Quantity(value + toAdd.value());
    }

    public int compareTo(final @NonNull Quantity toCompare){
        return value.compareTo(toCompare.value());
    }

    public boolean equals(final Object obj){
        if (this == obj) return true;
        if (!(obj instanceof Quantity toCompare)) return false;
        return compareTo(toCompare) == 0;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    @NonNull
    public String toString() {
        return value.toString();
    }

}
