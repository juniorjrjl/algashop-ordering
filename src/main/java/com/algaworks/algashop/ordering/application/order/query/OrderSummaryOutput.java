package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class OrderSummaryOutput {

    private String id;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;
    private String orderStatus;
    private String paymentMethod;
    private CustomerMinimalOutput customer;

    public OrderSummaryOutput(final Long id,
                              final Integer totalItems,
                              final BigDecimal totalAmount,
                              final OffsetDateTime placedAt,
                              final OffsetDateTime paidAt,
                              final OffsetDateTime canceledAt,
                              final OffsetDateTime readyAt,
                              final String orderStatus,
                              final String paymentMethod,
                              final CustomerMinimalOutput customer) {
        this.id = new OrderId(id).toString();
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.placedAt = placedAt;
        this.paidAt = paidAt;
        this.canceledAt = canceledAt;
        this.readyAt = readyAt;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.customer = customer;
    }
}
