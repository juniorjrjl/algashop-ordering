package com.algaworks.algashop.ordering.domain.utility.databuilder;

import com.algaworks.algashop.ordering.domain.entity.Order;
import com.algaworks.algashop.ordering.domain.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.valueobject.BillingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.ShippingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class OrderDataBuilder {

    private static final CustomFaker customFaker = new CustomFaker();

    @With
    private Supplier<OrderId> id = () -> customFaker.valueObject().orderId();
    @With
    private Supplier<CustomerId> customerId = () -> customFaker.valueObject().customerId();
    @With
    private Supplier<Money> totalAmount = () -> customFaker.valueObject().money();
    @With
    private Supplier<Quantity> totalItems = () -> customFaker.valueObject().quantity();
    @With
    private Supplier<OffsetDateTime> placedAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> paidAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> canceledAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> readyAt = OffsetDateTime::now;
    @With
    private Supplier<BillingInfo> billing = () -> BillingInfoDataBuilder.builder().buildNew();
    @With
    private Supplier<ShippingInfo> shipping = () -> ShippingInfoDataBuilder.builder().buildNew();
    @With
    private Supplier<OrderStatus> orderStatus = () -> customFaker.options().option(OrderStatus.class);
    @With
    private Supplier<PaymentMethod> paymentMethod = () -> customFaker.options().option(PaymentMethod.class);
    @With
    private Supplier<Money> shippingCost = () -> customFaker.valueObject().money();
    @With
    private Supplier<LocalDate> expectedDeliveryDate = () -> customFaker.timeAndDate().birthday();
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
        final var shippingCost = order.shippingCost();
        final var expectedDeliveryDate = order.expectedDeliveryDate();
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
                () -> shippingCost,
                () -> expectedDeliveryDate,
                () -> items
        );
    }

    public Order build(){
        return Order.existing()
                .id(id.get())
                .customerId(customerId.get())
                .totalAmount(totalAmount.get())
                .totalItems(totalItems.get())
                .placedAt(placedAt.get())
                .paidAt(paidAt.get())
                .canceledAt(canceledAt.get())
                .readyAt(readyAt.get())
                .billing(billing.get())
                .shipping(shipping.get())
                .orderStatus(orderStatus.get())
                .paymentMethod(paymentMethod.get())
                .shippingCost(shippingCost.get())
                .expectedDeliveryDate(expectedDeliveryDate.get())
                .items(items.get())
                .build();
    }

}
