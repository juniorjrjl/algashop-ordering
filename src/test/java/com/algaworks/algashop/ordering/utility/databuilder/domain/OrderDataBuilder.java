package com.algaworks.algashop.ordering.utility.databuilder.domain;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderItem;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class OrderDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<OrderId> id = OrderId::new;
    @With
    private Supplier<CustomerId> customerId = CustomerId::new;
    private Supplier<Money> totalAmount;
    private Supplier<Quantity> totalItems;
    @With
    private Supplier<OffsetDateTime> placedAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> paidAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> canceledAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> readyAt = OffsetDateTime::now;
    @With
    private Supplier<Billing> billing = () -> customFaker.order().billing();
    @With
    private Supplier<Shipping> shipping = () -> customFaker.order().shipping();
    @With
    private Supplier<OrderStatus> orderStatus = () -> customFaker.options().option(OrderStatus.class);
    @With
    private Supplier<PaymentMethod> paymentMethod = () -> customFaker.options().option(PaymentMethod.class);
    @With
    private Supplier<Set<OrderItem>> items = () -> OrderItemDataBuilder.builder()
            .buildExistingList(customFaker.number().numberBetween(1, 9));

    public static OrderDataBuilder builder() {
        return new OrderDataBuilder();
    }

    public static OrderDataBuilder builder(final Order order) {
        final var id = order.id();
        final var customerId = order.customerId();
        final var totalAmount = order.totalAmount();
        final var totalItems = order.totalItems();
        final var placedAt = order.placedAt();
        final var paidAt = order.paidAt();
        final var canceledAt = order.canceledAt();
        final var readyAt = order.readyAt();
        final var billing = order.billing();
        final var shipping = order.shipping();
        final var orderStatus = order.orderStatus();
        final var paymentMethod = order.paymentMethod();
        final var items = new HashSet<>(order.items());
        return new OrderDataBuilder(
                () -> id,
                () -> customerId,
                () -> totalAmount,
                () -> totalItems,
                () -> placedAt,
                () -> paidAt,
                () -> canceledAt,
                () -> readyAt,
                () -> billing,
                () -> shipping,
                () -> orderStatus,
                () -> paymentMethod,
                () -> items
        );
    }

    public Order buildExisting(){
        final var genItems = this.items.get();
        final var genShipping = shipping.get();
        final var cost = Optional.ofNullable(genShipping)
                .map(Shipping::cost)
                .orElse(Money.ZERO);
        final var genTotalItems = genItems.stream()
                .map(OrderItem::quantity)
                .reduce(Quantity.ZERO, Quantity::add);
        final var genTotalAmount = genItems.stream()
                .map(OrderItem::totalAmount)
                .reduce(cost, Money::add);
        return Order.existing()
                .id(id.get())
                .customerId(customerId.get())
                .totalAmount(genTotalAmount)
                .totalItems(genTotalItems)
                .placedAt(placedAt.get())
                .paidAt(paidAt.get())
                .canceledAt(canceledAt.get())
                .readyAt(readyAt.get())
                .billing(billing.get())
                .shipping(genShipping)
                .orderStatus(orderStatus.get())
                .paymentMethod(paymentMethod.get())
                .items(genItems)
                .build();
    }

}
