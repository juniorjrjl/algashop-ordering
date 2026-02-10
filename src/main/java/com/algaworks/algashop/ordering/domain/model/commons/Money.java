package com.algaworks.algashop.ordering.domain.model.commons;

import com.algaworks.algashop.ordering.domain.model.RichComparable;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Objects.requireNonNull;

public record Money(BigDecimal value) implements RichComparable<Money> {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money{
        if (requireNonNull(value).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        value = value.setScale(2, HALF_EVEN);
    }

    public Money(final String value){
        requireNonNull(value);
        var moneyValue = BigDecimal.ONE;
        try{
            moneyValue = new BigDecimal(value);
        } catch (NumberFormatException _){
            throw new IllegalArgumentException();
        }
        this(moneyValue);
    }

    public Money add(final Money toAdd){
        return new Money(value.add(toAdd.value()));
    }

    public Money multiply(final Quantity quantity){
        return new Money(value.multiply(new BigDecimal(quantity.value())));
    }

    public Money divide(final Money toDivide){
        return new Money(value.divide(toDivide.value(), HALF_EVEN));
    }

    public int compareTo(final Money toCompare){
        return value.compareTo(toCompare.value());
    }

    public boolean equals(final Object obj){
        if (this == obj) return true;
        if (!(obj instanceof Money toCompare)) return false;
        return compareTo(toCompare) == 0;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value().toPlainString();
    }

}
