package com.algaworks.algashop.ordering.application.shoppingcart.query;

import java.util.UUID;

public interface ShoppingCartQueryService {

    ShoppingCartOutput findById(final UUID id);

    ShoppingCartOutput findByCustomerId(final UUID customerId);

}
