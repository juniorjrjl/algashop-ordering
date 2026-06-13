package com.algaworks.algashop.ordering.core.port.in.shoppingcart;

import java.util.UUID;

public interface ForQueryingShoppingCart {

    ShoppingCartOutput findById(final UUID id);

    ShoppingCartOutput findByCustomerId(final UUID customerId);

}
