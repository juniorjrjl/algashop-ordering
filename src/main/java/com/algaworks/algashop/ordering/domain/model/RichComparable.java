package com.algaworks.algashop.ordering.domain.model;

public interface RichComparable<T> extends Comparable<T> {

    @Override
    int compareTo(final T toCompare);

    default boolean isLessThan(final T toCompare){
        return compareTo(toCompare) < 0;
    }

    default boolean isLessThanOrEqualTo(final T toCompare){
        return compareTo(toCompare) <= 0;
    }

    default boolean isGreaterThan(final T toCompare){
        return compareTo(toCompare) > 0;
    }

    default boolean isGreaterThanOrEqualTo(final T toCompare){
        return compareTo(toCompare) >= 0;
    }

}
