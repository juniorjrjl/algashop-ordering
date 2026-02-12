package com.algaworks.algashop.ordering.application.shoppingcart.notification;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotifyShoppingCartItemRemovedInput(
        String shoppingCartId,
        UUID customerId,
        UUID productId,
        OffsetDateTime removedAt
) {
}
