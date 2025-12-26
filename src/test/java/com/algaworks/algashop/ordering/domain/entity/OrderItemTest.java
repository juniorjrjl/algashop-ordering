package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderItemTest {

    private static final CustomFaker faker = new CustomFaker();

    @Test
    void shouldCreateBrandNew(){
        final var orderId = faker.valueObject().orderId();
        final var productId = faker.valueObject().productId();
        final var productName = faker.valueObject().productName();
        final var price = faker.valueObject().money();
        final var quantity = faker.valueObject().quantity();
        final var orderItem = OrderItem.brandNew()
                .orderId(orderId)
                .productId(productId)
                .productName(productName)
                .price(price)
                .quantity(quantity)
                .build();
        assertWith(orderItem,
                o -> assertThat(o.id()).isNotNull(),
                o -> assertThat(o.orderId()).isEqualTo(orderId),
                o -> assertThat(o.productId()).isEqualTo(productId),
                o -> assertThat(o.productName()).isEqualTo(productName),
                o -> assertThat(o.price()).isEqualTo(price),
                o -> assertThat(o.quantity()).isEqualTo(quantity),
                o -> assertThat(o.totalAmount()).isNotNull()
                );
    }

    @Test
    void shouldCreateExisting(){
        final var id = faker.valueObject().orderItemId();
        final var orderId = faker.valueObject().orderId();
        final var productId = faker.valueObject().productId();
        final var productName = faker.valueObject().productName();
        final var price = faker.valueObject().money();
        final var quantity = faker.valueObject().quantity();
        final var totalAmount = faker.valueObject().money();
        final var orderItem = OrderItem.existing()
                .id(id)
                .orderId(orderId)
                .productId(productId)
                .productName(productName)
                .price(price)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .build();
        assertWith(orderItem,
                o -> assertThat(o.id()).isEqualTo(id),
                o -> assertThat(o.orderId()).isEqualTo(orderId),
                o -> assertThat(o.productId()).isEqualTo(productId),
                o -> assertThat(o.productName()).isEqualTo(productName),
                o -> assertThat(o.price()).isEqualTo(price),
                o -> assertThat(o.quantity()).isEqualTo(quantity),
                o -> assertThat(o.totalAmount()).isEqualTo(totalAmount)
        );
    }

}