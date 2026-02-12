package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.CANCELED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.DRAFT;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.PAID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@UnitTest
class OrderTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldCreateDraft(){
        final var customerId = new CustomerId();
        final var order = Order.draft(customerId);
        final var nullProps = new String[]{
                "id",
                "customerId",
                "totalAmount",
                "totalItems",
                "orderStatus",
                "items",
                "domainEvents"
        };
        assertWith(order,
                o -> assertThat(o.id()).isNotNull(),
                o -> assertThat(o.customerId()).isEqualTo(customerId),
                o -> assertThat(o.totalAmount()).isEqualTo(Money.ZERO),
                o -> assertThat(o.totalItems()).isEqualTo(Quantity.ZERO),
                o -> assertThat(o).hasAllNullFieldsOrPropertiesExcept(nullProps),
                o -> assertThat(o.isDraft()).isTrue(),
                o -> assertThat(o.items()).isEmpty()
                );
    }

    @Test
    void shouldAddItem(){
        final var customerId = new  CustomerId();
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity = customFaker.common().quantity();
        final var order = Order.draft(customerId);
        order.addItem(product, quantity);

        assertThat(order.items()).hasSize(1);

        assertWith(order.items().iterator().next(),
                i -> assertThat(i.id()).isNotNull(),
                i -> assertThat(i.orderId()).isEqualTo(order.id()),
                i -> assertThat(i.productId()).isEqualTo(product.id()),
                i -> assertThat(i.productName()).isEqualTo(product.name()),
                i -> assertThat(i.price()).isEqualTo(product.price()),
                i -> assertThat(i.quantity()).isEqualTo(quantity)
                );
    }

    @Test
    void shouldThrowExceptionWhenTryModifyListUsingGet(){
        final var order = OrderDataBuilder.builder().buildExisting();
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> order.items().clear());
    }

    @Test
    void givenOrderToAddItemsShouldCalculateTotals(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).buildExisting();
        final var product1 = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity1 = customFaker.common().quantity(1, 10);
        order.addItem(product1, quantity1);
        final var product2 = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity2 = customFaker.common().quantity(1, 10);
        order.addItem(product2, quantity2);

        final var shippingCost = Optional.ofNullable(order.shipping())
                .map(Shipping::cost)
                .orElse(Money.ZERO);
        final var totalProduct1 = product1.price().multiply(quantity1);
        final var totalProduct2 = product2.price().multiply(quantity2);
        final var expectedTotalAmount = totalProduct1
                .add(totalProduct2)
                .add(shippingCost);
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        final var expectedQuantity = quantity1.add(quantity2);
        assertThat(order.totalItems()).isEqualTo(expectedQuantity);
    }

    @Test
    void givenDraftOrderWhenPlaceShouldChangeToPlaced(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .buildExisting();
        IntStream.range(1, 10).forEach(_ -> order.addItem(
                ProductDataBuilder.builder().withInStock(() -> true).build(),
                customFaker.common().quantity(1, 10)
        ));
        order.place();
        assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenPlacedOrderWhenTryToPlaceShouldThrowException(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withOrderStatus(() -> DRAFT)
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .buildExisting();
        IntStream.range(1, 10).forEach(_ -> order.addItem(
                ProductDataBuilder.builder().withInStock(() -> true).build(),
                customFaker.common().quantity(1, 10)
        ));
        order.place();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::place);
    }

    @Test
    void givenPlacedOrderWhenMarkAsPaidShouldChangeToPaid(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .buildExisting();
        IntStream.range(1, 10).forEach(_ -> order.addItem(
                ProductDataBuilder.builder().withInStock(() -> true).build(),
                customFaker.common().quantity(1, 10)
        ));
        order.place();
        order.markAsPaid();
        assertWith(order,
                o-> assertThat(o.isPaid()).isTrue(),
                o -> assertThat(o.paidAt()).isNotNull()
                );
    }

    @Test
    void givenDraftOrderWhenChangePaymentMethodShouldAllowChange(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.order().shipping())
                .buildExisting();
        final var newPaymentMethod = customFaker.options().option(PaymentMethod.class);
        order.changePaymentMethod(newPaymentMethod);
        assertThat(order.paymentMethod()).isEqualTo(newPaymentMethod);
    }

    @Test
    void givenDraftOrderWhenChangeBillingInfoShouldAllowChange(){
        final var billing = customFaker.order().billing();
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).buildExisting();
        order.changeBilling(billing);
        assertThat(order.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrderWhenChangeShippingInfoShouldAllowChange(){
        final var shipping = customFaker.order().shipping();
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.order().shipping())
                .buildExisting();
        final var expectedTotalAmount = new Money(order
                .totalAmount()
                .value()
                .subtract(order.shipping().cost().value()));
        order.changeShipping(shipping);
        assertThat(order.shipping()).isEqualTo(shipping);
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount.add(shipping.cost()));
    }

    @Test
    void givenDraftOrderAndOrderDeliveryDateInThePastWhenChangeShippingInfoShouldNotAllowChange(){
        final var shipping = customFaker.order()
                .shipping()
                .toBuilder()
                .expectedDate(customFaker.timeAndDate().birthday())
                .build();
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).buildExisting();
        assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(() -> order.changeShipping(shipping));
    }

    @Test
    void givenDraftOrderAndAddOrderItemWhenChangeItemShouldRecalculate(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.order().shipping())
                .buildExisting();
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity = customFaker.common().quantity(1, 10);
        order.addItem(product ,quantity);
        final var orderItemWithId = order.items().iterator().next();
        final var newQuantity = customFaker.common().quantity(1, 9).add(quantity);
        order.changeItemQuantity(orderItemWithId.id(), newQuantity);

        final var expectedAmount = product.price().multiply(newQuantity);
        assertWith(order,
                o -> assertThat(o.totalAmount()).isEqualTo(expectedAmount.add(order.shipping().cost())),
                o -> assertThat(o.totalItems()).isEqualTo(newQuantity)
                );
    }

    @Test
    void givenDraftOrderWhenChangeNonExistingItemShouldThrowException(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .buildExisting();
        final var orderItemId = new OrderItemId();
        final var quantity = customFaker.common().quantity(1, 10);
        assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.changeItemQuantity(orderItemId, quantity));

    }

    @Test
    void givenDraftOrderWhenTryAddProductOutOfStockShouldThrowError(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .buildExisting();
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> false)
                .build();
        final var quantity = customFaker.common().quantity(1, 10);
        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> order.addItem(product, quantity));
    }

    private static final List<Consumer<Order>> givenPlacedOrderWhenTryEditItShouldThrowException
            = List.of(
                    o -> o.addItem(
                            ProductDataBuilder.builder()
                                    .withInStock(() -> true)
                                    .build(),
                            customFaker.common().quantity()
                    ),
            o -> o.changePaymentMethod(customFaker.options().option(PaymentMethod.class)),
            o -> o.changeBilling(customFaker.order().billing()),
            o -> o.changeShipping(customFaker.order().shipping()),
            o -> o.changeItemQuantity(o.items().iterator().next().id(), customFaker.common().quantity()),
            o -> o.removeItem(o.items().iterator().next().id())
    );

    @ParameterizedTest
    @FieldSource
    void givenPlacedOrderWhenTryEditItShouldThrowException(final Consumer<Order> orderAction){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .buildExisting();
        order.addItem(
                ProductDataBuilder.builder()
                        .withInStock(() -> true)
                        .build(),
                customFaker.common().quantity()
        );
        order.place();
        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> orderAction.accept(order));
    }

    @Test
    void givenDraftOrderWhenRemoveExistingItemShouldRemoveIt(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.order().shipping())
                .buildExisting();
        ProductDataBuilder.builder()
                .withInStock(() -> true)
                .buildList(2)
                .forEach(p -> order.addItem(p, customFaker.common().quantity(1, 10)));
        final var orderItemsList = order.items().stream().toList();
        final OrderItem toRemove;
        final OrderItem keepInOrder;
        if (customFaker.bool().bool()) {
            toRemove = orderItemsList.getFirst();
            keepInOrder = orderItemsList.getLast();
        } else {
            keepInOrder = orderItemsList.getFirst();
            toRemove = orderItemsList.getLast();
        }
        order.removeItem(toRemove.id());
        final var productAmount = keepInOrder.price().multiply(keepInOrder.quantity());
        assertWith(order,
                o -> assertThat(o.items()).containsOnly(keepInOrder),
                o -> assertThat(o.totalAmount()).isEqualTo(productAmount.add(o.shipping().cost())),
                o -> assertThat(o.totalItems()).isEqualTo(keepInOrder.quantity())
        );
    }

    @Test
    void givenDraftOrderWhenRemoveNonExistingItemShouldThrowException(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.order().shipping())
                .buildExisting();
        ProductDataBuilder.builder()
                .withInStock(() -> true)
                .buildList(2)
                .forEach(p -> order.addItem(p, customFaker.common().quantity(1, 10)));
        assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(new OrderItemId()));
    }

    @Test
    void givenPlacedOrderWhenTryChangeToReadyShouldAllowIt(){
        final var order = OrderDataBuilder.builder()
                .withOrderStatus(() -> PAID)
                .buildExisting();
        order.markAsReady();
        assertThat(order.readyAt()).isNotNull();
        assertThat(order.isReady()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PAID", "READY"}, mode = EXCLUDE)
    void givenNonPaidOrderWhenTryChangeToReadyShouldAllowIt(final OrderStatus orderStatus){
        final var order = OrderDataBuilder.builder()
                .withOrderStatus(() -> orderStatus)
                .buildExisting();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsReady);
        assertThat(order.isReady()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = "CANCELED", mode = EXCLUDE)
    void givenNonCanceledOrderWhenTryCancelShouldAllowIt(final OrderStatus orderStatus){
        final var order = OrderDataBuilder.builder()
                .withOrderStatus(() -> orderStatus)
                .buildExisting();
        order.cancel();
        assertThat(order.canceledAt()).isNotNull();
        assertThat(order.isCanceled()).isTrue();
    }

    @Test
    void givenCanceledOrderWhenTryCancelAgainShouldThrowException(){
        final var order = OrderDataBuilder.builder()
                .withOrderStatus(() -> CANCELED)
                .buildExisting();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::cancel);
    }

}