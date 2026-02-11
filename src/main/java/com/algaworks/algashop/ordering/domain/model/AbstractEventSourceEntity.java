package com.algaworks.algashop.ordering.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractEventSourceEntity implements DomainEventSource {

    protected final List<Object> domainEvents = new ArrayList<>();

    protected void publishDomainEvent(final Object event){
        this.domainEvents.add(event);
    }

    @Override
    public List<Object> domainEvents(){
        return Collections.unmodifiableList(this.domainEvents);
    }

    public void clearDomainEvents(){
        this.domainEvents.clear();
    }

}
