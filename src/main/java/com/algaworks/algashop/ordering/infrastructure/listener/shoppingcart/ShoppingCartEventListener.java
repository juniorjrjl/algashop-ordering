package com.algaworks.algashop.ordering.infrastructure.listener.shoppingcart;

import com.algaworks.algashop.ordering.application.shoppingcart.notification.NotifyShoppingCartCreatedInput;
import com.algaworks.algashop.ordering.application.shoppingcart.notification.NotifyShoppingCartEmptiedInput;
import com.algaworks.algashop.ordering.application.shoppingcart.notification.NotifyShoppingCartItemAddedInput;
import com.algaworks.algashop.ordering.application.shoppingcart.notification.NotifyShoppingCartItemRemovedInput;
import com.algaworks.algashop.ordering.application.shoppingcart.notification.ShoppingCartNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShoppingCartEventListener {

    private final ShoppingCartNotificationApplicationService notificationApplicationService;

    @EventListener(ShoppingCartCreatedEvent.class)
    public void listener(final ShoppingCartCreatedEvent event){
        final var input = new NotifyShoppingCartCreatedInput(
                event.shoppingCartId().toString(),
                event.customerId().value(),
                event.createdAt()
        );
        notificationApplicationService.notifyStartShopping(input);
    }

    @EventListener(ShoppingCartItemAddedEvent.class)
    public void listener(final ShoppingCartItemAddedEvent event){
        final var input = new NotifyShoppingCartItemAddedInput(
                event.shoppingCartId().toString(),
                event.customerId().value(),
                event.productId().value(),
                event.addedAt()
        );
        notificationApplicationService.notifyAddItem(input);
    }

    @EventListener(ShoppingCartItemRemovedEvent.class)
    public void listener(final ShoppingCartItemRemovedEvent event){
        final var input = new NotifyShoppingCartItemRemovedInput(
                event.shoppingCartId().toString(),
                event.customerId().value(),
                event.productId().value(),
                event.removedAt()
        );
        notificationApplicationService.notifyRemoveItem(input);
    }

    @EventListener(ShoppingCartEmptiedEvent.class)
    public void listener(final ShoppingCartEmptiedEvent event){
        final var input = new NotifyShoppingCartEmptiedInput(
                event.shoppingCartId().toString(),
                event.customerId().value(),
                event.emptiedAt()
        );
        notificationApplicationService.notifyRemoveAllItems(input);
    }
}
