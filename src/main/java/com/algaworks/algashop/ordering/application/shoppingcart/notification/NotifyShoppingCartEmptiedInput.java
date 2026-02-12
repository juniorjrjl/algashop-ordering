package com.algaworks.algashop.ordering.application.shoppingcart.notification;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotifyShoppingCartEmptiedInput(
        String shoppingCartId,
        UUID customerId,
        OffsetDateTime emptiedAt
) {
}
