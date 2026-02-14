package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.RichComparable;

import static java.util.Objects.requireNonNull;

public record LoyaltyPoints(Integer value) implements RichComparable<LoyaltyPoints> {

    public static final LoyaltyPoints ZERO = new LoyaltyPoints();
    public static final LoyaltyPoints BASE_POINTS = new LoyaltyPoints(5);

    private LoyaltyPoints() {
        this(0);
    }

    public LoyaltyPoints {
        if (value < 0){
            throw new IllegalArgumentException();
        }
    }

    public LoyaltyPoints add(final Integer value){
        if (value < 0){
            throw new IllegalArgumentException();
        }
        return new LoyaltyPoints(this.value() + value);
    }

    public LoyaltyPoints add(final LoyaltyPoints loyaltyPoints){
        return add(requireNonNull(loyaltyPoints).value());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(final LoyaltyPoints o) {
        return this.value().compareTo(o.value);
    }
}
