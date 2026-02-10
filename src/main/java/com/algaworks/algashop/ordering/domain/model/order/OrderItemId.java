package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.IdGenerator;
import io.hypersistence.tsid.TSID;

import static java.util.Objects.requireNonNull;

public record OrderItemId(TSID value) {

    public OrderItemId(){
        this(IdGenerator.generateTSID());
    }

    public OrderItemId {
        requireNonNull(value);
    }

    public OrderItemId(final Long value){
        this(TSID.from(value));
    }

    public OrderItemId(final String value){
        this(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
