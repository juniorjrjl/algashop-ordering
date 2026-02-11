package com.algaworks.algashop.ordering.application.order.notification;

import io.hypersistence.tsid.TSID;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotifyOrderPaidInput(
        TSID orderId,
        UUID customerId,
        OffsetDateTime paidAt
) {
}
