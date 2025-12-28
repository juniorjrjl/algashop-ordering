package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.exception.OutOfStockException;
import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.utility.databuilder.BillingDataBuilder;
import com.algaworks.algashop.ordering.domain.utility.databuilder.OrderDataBuilder;
import com.algaworks.algashop.ordering.domain.utility.databuilder.OrderItemDataBuilder;
import com.algaworks.algashop.ordering.domain.utility.databuilder.ProductDataBuilder;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderItemId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.algaworks.algashop.ordering.domain.entity.OrderStatus.DRAFT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class OrderTest {

    private static final CustomFaker customFaker = new CustomFaker();

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
                "items"
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
        final var quantity = customFaker.valueObject().quantity();
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
        final var order = OrderDataBuilder.builder().build();
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> order.items().clear());
    }

    @Test
    void givenOrderToAddItemsShouldCalculateTotals(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).build();
        final var product1 = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity1 = customFaker.valueObject().quantity(1, 10);
        order.addItem(product1, quantity1);
        final var product2 = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity2 = customFaker.valueObject().quantity(1, 10);
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
                .withShipping(() -> customFaker.valueObject().shipping())
                .withBilling(() -> BillingDataBuilder.builder().build())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .withItems(() -> OrderItemDataBuilder.builder()
                        .buildExistingList(customFaker.number().numberBetween(1, 9)))
                .build();
        order.place();
        assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenPlacedOrderWhenTryToPlaceShouldThrowException(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withOrderStatus(() -> DRAFT)
                .withShipping(() -> customFaker.valueObject().shipping())
                .withBilling(() -> BillingDataBuilder.builder().build())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .withItems(() -> OrderItemDataBuilder.builder()
                        .buildExistingList(customFaker.number().numberBetween(1, 9)))
                .build();
        order.place();
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::place);
    }

    @Test
    void givenPlacedOrderWhenMarkAsPaidShouldChangeToPaid(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.valueObject().shipping())
                .withBilling(() -> BillingDataBuilder.builder().build())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .withItems(() -> OrderItemDataBuilder.builder()
                        .buildExistingList(customFaker.number().numberBetween(1, 9)))
                .build();
        order.place();
        order.markAsPaid();
        assertWith(order,
                o-> assertThat(o.isPaid()).isTrue(),
                o -> assertThat(o.paidAt()).isNotNull()
                );
    }

    @Test
    void givenDraftOrderWhenChangePaymentMethodShouldAllowChange(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).build();
        final var newPaymentMethod = customFaker.options().option(PaymentMethod.class);
        order.changePaymentMethod(newPaymentMethod);
        assertThat(order.paymentMethod()).isEqualTo(newPaymentMethod);
    }

    @Test
    void givenDraftOrderWhenChangeBillingInfoShouldAllowChange(){
        final var billing = BillingDataBuilder.builder().build();
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).build();
        order.changeBilling(billing);
        assertThat(order.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrderWhenChangeShippingInfoShouldAllowChange(){
        final var shipping = customFaker.valueObject().shipping();
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).build();
        order.changeShipping(shipping);
        assertThat(order.shipping()).isEqualTo(shipping);
    }

    @Test
    void givenDraftOrderAndOrderDeliveryDateInThePastWhenChangeShippingInfoShouldNotAllowChange(){
        final var shipping = customFaker.valueObject()
                .shipping()
                .toBuilder()
                .expectedDate(customFaker.timeAndDate().birthday())
                .build();
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).build();
        assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(() -> order.changeShipping(shipping));
    }

    @Test
    void givenDraftOrderAndAddOrderItemWhenChangeItemShouldRecalculate(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .build();
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity = customFaker.valueObject().quantity(1, 10);
        order.addItem(product ,quantity);
        final var orderItemWithId = order.items().iterator().next();
        final var newQuantity = customFaker.valueObject().quantity(1, 9).add(quantity);
        order.changeItemQuantity(orderItemWithId.id(), newQuantity);

        final var expectedAmount = product.price().multiply(newQuantity);
        assertWith(order,
                o -> assertThat(o.totalAmount()).isEqualTo(expectedAmount),
                o -> assertThat(o.totalItems()).isEqualTo(newQuantity)
                );
    }

    @Test
    void givenDraftOrderWhenChangeNonExistingItemShouldThrowException(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .build();
        final var orderItemId = new OrderItemId();
        final var quantity = customFaker.valueObject().quantity(1, 10);
        assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.changeItemQuantity(orderItemId, quantity));

    }

    @Test
    void givenDraftOrderWhenTryAddProductOutOfStockShouldThrowError(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .build();
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> false)
                .build();
        final var quantity = customFaker.valueObject().quantity(1, 10);
        assertThatExceptionOfType(OutOfStockException.class)
                .isThrownBy(() -> order.addItem(product, quantity));
    }

    private static final List<Consumer<Order>> givenPlacedOrderWhenTryEditItShouldThrowException
            = List.of(
                    o -> o.addItem(
                            ProductDataBuilder.builder()
                                    .withInStock(() -> true)
                                    .build(),
                            customFaker.valueObject().quantity()
                    ),
            o -> o.changePaymentMethod(customFaker.options().option(PaymentMethod.class)),
            o -> o.changeBilling(BillingDataBuilder.builder().build()),
            o -> o.changeShipping(customFaker.valueObject().shipping()),
            o -> o.changeItemQuantity(o.items().iterator().next().id(), customFaker.valueObject().quantity()),
            o -> o.removeItem(o.items().iterator().next().id())
    );

    @ParameterizedTest
    @FieldSource
    void givenPlacedOrderWhenTryEditItShouldThrowException(final Consumer<Order> orderAction){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.valueObject().shipping())
                .withBilling(() -> BillingDataBuilder.builder().build())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .build();
        order.addItem(
                ProductDataBuilder.builder()
                        .withInStock(() -> true)
                        .build(),
                customFaker.valueObject().quantity()
        );
        order.place();
        assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> orderAction.accept(order));
    }

    @Test
    void givenDraftOrderWhenRemoveExistingItemShouldRemoveIt(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> customFaker.valueObject().shipping())
                .build();
        ProductDataBuilder.builder()
                .withInStock(() -> true)
                .buildList(2)
                .forEach(p -> order.addItem(p, customFaker.valueObject().quantity(1, 10)));
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
                .withShipping(() -> customFaker.valueObject().shipping())
                .build();
        ProductDataBuilder.builder()
                .withInStock(() -> true)
                .buildList(2)
                .forEach(p -> order.addItem(p, customFaker.valueObject().quantity(1, 10)));
        assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(new OrderItemId()));
    }

}