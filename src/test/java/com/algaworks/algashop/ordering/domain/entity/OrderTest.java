package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.utility.databuilder.BillingInfoDataBuilder;
import com.algaworks.algashop.ordering.domain.utility.databuilder.OrderDataBuilder;
import com.algaworks.algashop.ordering.domain.utility.databuilder.OrderItemDataBuilder;
import com.algaworks.algashop.ordering.domain.utility.databuilder.ShippingInfoDataBuilder;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static com.algaworks.algashop.ordering.domain.entity.OrderStatus.DRAFT;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class OrderTest {

    private static final CustomFaker customFaker = new CustomFaker();

    @Test
    void shouldCreateDraft(){
        final var customerId = customFaker.valueObject().customerId();
        final var order = Order.draft(customerId);
        final var nullProps = new String[]{
                "id",
                "customerId",
                "totalAmount",
                "totalItems",
                "orderStatus",
                "shippingCost",
                "items"
        };
        assertWith(order,
                o -> assertThat(o.id()).isNotNull(),
                o -> assertThat(o.customerId()).isEqualTo(customerId),
                o -> assertThat(o.totalAmount()).isEqualTo(Money.ZERO),
                o -> assertThat(o.totalItems()).isEqualTo(Quantity.ZERO),
                o -> assertThat(o).hasAllNullFieldsOrPropertiesExcept(nullProps),
                o -> assertThat(o.orderStatus()).isEqualTo(DRAFT),
                o -> assertThat(o.shippingCost()).isEqualTo(Money.ZERO),
                o -> assertThat(o.items()).isEmpty()
                );
    }

    @Test
    void shouldAddItem(){
        final var customerId = customFaker.valueObject().customerId();
        final var productId = customFaker.valueObject().productId();
        final var productName = customFaker.valueObject().productName();
        final var price = customFaker.valueObject().money();
        final var quantity = customFaker.valueObject().quantity();
        final var order = Order.draft(customerId);
        order.addItem(productId, productName, price, quantity);

        assertThat(order.items()).hasSize(1);

        assertWith(order.items().iterator().next(),
                i -> assertThat(i.id()).isNotNull(),
                i -> assertThat(i.orderId()).isEqualTo(order.id()),
                i -> assertThat(i.productId()).isEqualTo(productId),
                i -> assertThat(i.productName()).isEqualTo(productName),
                i -> assertThat(i.price()).isEqualTo(price),
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
        final var orderItem1 = OrderItemDataBuilder.builder().buildNew();
        order.addItem(
                orderItem1.productId(),
                orderItem1.productName(),
                orderItem1.price(),
                orderItem1.quantity()
        );
        final var orderItem2 = OrderItemDataBuilder.builder().buildNew();
        order.addItem(
                orderItem2.productId(),
                orderItem2.productName(),
                orderItem2.price(),
                orderItem2.quantity()
        );

        final var shippingCost = Optional.ofNullable(order.shippingCost())
                .orElse(Money.ZERO);
        final var expectedTotalAmount = orderItem1.totalAmount()
                .add(orderItem2.totalAmount())
                .add(shippingCost);
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        final var expectedQuantity = orderItem1.quantity().add(orderItem2.quantity());
        assertThat(order.totalItems()).isEqualTo(expectedQuantity);
    }

    @Test
    void givenDraftOrderWhenPlaceShouldChangeToPlaced(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withShipping(() -> ShippingInfoDataBuilder.builder().buildNew())
                .withBilling(() -> BillingInfoDataBuilder.builder().buildNew())
                .withExpectedDeliveryDate(() -> customFaker.timeAndDate().birthday())
                .withShippingCost(() -> customFaker.valueObject().money())
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
                .withShipping(() -> ShippingInfoDataBuilder.builder().buildNew())
                .withBilling(() -> BillingInfoDataBuilder.builder().buildNew())
                .withExpectedDeliveryDate(() -> customFaker.timeAndDate().birthday())
                .withShippingCost(() -> customFaker.valueObject().money())
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
                .withShipping(() -> ShippingInfoDataBuilder.builder().buildNew())
                .withBilling(() -> BillingInfoDataBuilder.builder().buildNew())
                .withExpectedDeliveryDate(() -> customFaker.timeAndDate().birthday())
                .withShippingCost(() -> customFaker.valueObject().money())
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
        final var billing = BillingInfoDataBuilder.builder().buildNew();
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).build();
        order.changeBillingInfo(billing);
        assertThat(order.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrderWhenChangeShippingInfoShouldAllowChange(){
        final var shipping = ShippingInfoDataBuilder.builder().buildNew();
        final var money = customFaker.valueObject().money();
        final var expectedDeliveryDate = LocalDate.ofInstant(customFaker.timeAndDate().future(), UTC);
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).build();
        order.changeShippingInfo(shipping, money, expectedDeliveryDate);

        assertWith(order,
                o -> assertThat(o.shipping()).isEqualTo(shipping),
                o -> assertThat(o.shippingCost()).isEqualTo(money),
                o -> assertThat(o.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate)
                );
    }

    @Test
    void givenDraftOrderAndOrderDeliveryDateInThePastWhenChangeShippingInfoShouldNotAllowChange(){
        final var shipping = ShippingInfoDataBuilder.builder().buildNew();
        final var money = customFaker.valueObject().money();
        final var expectedDeliveryDate = LocalDate.ofInstant(customFaker.timeAndDate().past(), UTC);
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId())).build();
        assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(() -> order.changeShippingInfo(shipping, money, expectedDeliveryDate));
    }

}