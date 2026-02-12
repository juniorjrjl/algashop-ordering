package com.algaworks.algashop.ordering.application.shoppingcart.notification;

public interface ShoppingCartNotificationApplicationService {

    void notifyStartShopping(final NotifyShoppingCartCreatedInput input);

    void notifyAddItem(final NotifyShoppingCartItemAddedInput input);

    void notifyRemoveItem(final NotifyShoppingCartItemRemovedInput input);

    void notifyRemoveAllItems(final NotifyShoppingCartEmptiedInput input);

}
