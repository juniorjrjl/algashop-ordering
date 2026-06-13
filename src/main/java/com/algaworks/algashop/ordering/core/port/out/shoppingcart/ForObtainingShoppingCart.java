package com.algaworks.algashop.ordering.core.port.out.shoppingcart;

import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartOutput;

import java.util.UUID;

public interface ForObtainingShoppingCart {

    ShoppingCartOutput findById(final UUID id);

    ShoppingCartOutput findByCustomerId(final UUID customerId);

}
