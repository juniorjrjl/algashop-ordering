package com.algaworks.algashop.ordering.application.shoppingcart.notification;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotifyShoppingCartItemAddedInput(
        String shoppingCartId,
        UUID customerId,
        UUID productId,
        OffsetDateTime addedAt
) {
}
