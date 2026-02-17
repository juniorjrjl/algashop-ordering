package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.application.checkout.BillingData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailOutput {

    private String id;
    private CustomerMinimalOutput customer;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;
    private String orderStatus;
    private String paymentMethod;
    private ShippingData shipping;
    private BillingData billing;
    private List<OrderItemDataDetailOutput> items;

}
