package com.algaworks.algashop.ordering.core.domain.model;

public interface RemoveCapableRepository<T extends AggregateRoot<ID>, ID>
        extends Repository<T, ID>{

    void remove(final ID id);
    void remove(final T aggregate);

}
