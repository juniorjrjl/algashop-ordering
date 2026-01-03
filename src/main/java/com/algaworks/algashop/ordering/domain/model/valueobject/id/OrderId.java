package com.algaworks.algashop.ordering.domain.model.valueobject.id;

import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;
import io.hypersistence.tsid.TSID;
import org.jspecify.annotations.NonNull;

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
    @NonNull
    public String toString() {
        return value.toString();
    }
}
