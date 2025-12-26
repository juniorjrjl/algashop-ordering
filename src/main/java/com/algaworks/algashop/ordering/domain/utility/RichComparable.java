package com.algaworks.algashop.ordering.domain.utility;

import org.jspecify.annotations.NonNull;

public interface RichComparable<T> extends Comparable<T> {

    int compareTo(@NonNull final T toCompare);

    default boolean isLessThan(final T toCompare){
        return compareTo(toCompare) < 0;
    }

    default boolean isGreaterThan(final T toCompare){
        return compareTo(toCompare) > 0;
    }

}
