package com.algaworks.algashop.ordering.core.port.in.shoppingcart;

import java.util.UUID;

public interface ForManagingShoppingCart {

    UUID createNew(final UUID rawCustomerId);


    void addItem(final ShoppingCartItemInput input);


    void removeItem(final UUID rawId, UUID rawShoppingCartItemId);


    void empty(final UUID rawId);


    void delete(UUID rawId);

}
