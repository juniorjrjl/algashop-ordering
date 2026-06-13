package com.algaworks.algashop.ordering.infrastructure.adapter.in.listener.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShoppingCartEventListener {

    @EventListener(ShoppingCartCreatedEvent.class)
    public void listener(final ShoppingCartCreatedEvent event){

    }

    @EventListener(ShoppingCartItemAddedEvent.class)
    public void listener(final ShoppingCartItemAddedEvent event){

    }

    @EventListener(ShoppingCartItemRemovedEvent.class)
    public void listener(final ShoppingCartItemRemovedEvent event){

    }

    @EventListener(ShoppingCartEmptiedEvent.class)
    public void listener(final ShoppingCartEmptiedEvent event){

    }
}
