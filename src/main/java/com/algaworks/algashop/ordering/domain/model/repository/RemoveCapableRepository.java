package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.AggregateRoot;

public interface RemoveCapableRepository<T extends AggregateRoot<ID>, ID>
        extends Repository<T, ID>{

    void remove(final ID id);
    void remove(final T aggregate);

}
