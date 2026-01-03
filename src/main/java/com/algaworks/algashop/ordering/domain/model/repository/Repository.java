package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.AggregateRoot;

import java.util.Optional;

public interface Repository<T extends AggregateRoot<ID>, ID> {

    Optional<T> ofId(final ID id);
    boolean exists(final ID id);
    void add(final T aggregateRoot);
    long count();

}
