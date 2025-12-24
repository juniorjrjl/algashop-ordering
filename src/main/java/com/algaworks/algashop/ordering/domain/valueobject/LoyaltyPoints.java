package com.algaworks.algashop.ordering.domain.valueobject;

import org.jspecify.annotations.NonNull;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public record LoyaltyPoints(Integer value) implements Comparable<LoyaltyPoints> {

    public static final LoyaltyPoints ZERO = new LoyaltyPoints();

    private LoyaltyPoints() {
        this(0);
    }

    public LoyaltyPoints {
        if (isNull(value) || value < 0){
            throw new IllegalArgumentException();
        }
    }

    public LoyaltyPoints add(final Integer value){
        if (isNull(value) || value < 0){
            throw new IllegalArgumentException();
        }
        return new LoyaltyPoints(this.value() + value);
    }

    public LoyaltyPoints add(final LoyaltyPoints loyaltyPoints){
        return add(requireNonNull(loyaltyPoints).value());
    }

    @Override
    @NonNull
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(final LoyaltyPoints o) {
        return this.value().compareTo(o.value);
    }
}
