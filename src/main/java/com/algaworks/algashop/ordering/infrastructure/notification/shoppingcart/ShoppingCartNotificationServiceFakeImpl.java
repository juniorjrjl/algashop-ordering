package com.algaworks.algashop.ordering.infrastructure.notification.shoppingcart;

import com.algaworks.algashop.ordering.application.shoppingcart.notification.NotifyShoppingCartCreatedInput;
import com.algaworks.algashop.ordering.application.shoppingcart.notification.NotifyShoppingCartEmptiedInput;
import com.algaworks.algashop.ordering.application.shoppingcart.notification.NotifyShoppingCartItemAddedInput;
import com.algaworks.algashop.ordering.application.shoppingcart.notification.NotifyShoppingCartItemRemovedInput;
import com.algaworks.algashop.ordering.application.shoppingcart.notification.ShoppingCartNotificationApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShoppingCartNotificationServiceFakeImpl implements ShoppingCartNotificationApplicationService {

    @Override
    public void notifyStartShopping(final NotifyShoppingCartCreatedInput input) {
        log.info("Notify shopping cart started: {}", input);
    }

    @Override
    public void notifyAddItem(final NotifyShoppingCartItemAddedInput input) {
        log.info("Notify shopping cart added: {}", input);
    }

    @Override
    public void notifyRemoveItem(final NotifyShoppingCartItemRemovedInput input) {
        log.info("Notify shopping cart removed: {}", input);
    }

    @Override
    public void notifyRemoveAllItems(final NotifyShoppingCartEmptiedInput input) {
        log.info("Notify shopping cart emptied: {}", input);
    }
}
