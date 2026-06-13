package com.algaworks.algashop.ordering.core.port.out.order;

import com.algaworks.algashop.ordering.core.port.in.order.BillingData;
import com.algaworks.algashop.ordering.core.port.in.order.ShippingData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

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
    private UUID creditCardId;
    private ShippingData shipping;
    private BillingData billing;
    private List<OrderItemDataDetailOutput> items;

}
