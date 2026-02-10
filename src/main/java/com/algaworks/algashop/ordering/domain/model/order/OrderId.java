package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.IdGenerator;
import io.hypersistence.tsid.TSID;

import static java.util.Objects.requireNonNull;

public record OrderId(TSID value) {

    public OrderId {
        requireNonNull(value);
    }

    public OrderId(){
        this(IdGenerator.generateTSID());
    }

    public OrderId(final Long value){
        this(TSID.from(value));
    }

    public OrderId(final String value){
        this(TSID.from(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
