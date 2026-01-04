package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.domain.ProductDataBuilder;
import com.algaworks.algashop.ordering.domain.model.utility.tag.UnitTest;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@UnitTest
class OrderItemTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldCreateBrandNew(){
        final var orderId = new OrderId();
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity = customFaker.valueObject().quantity(1, 10);
        final var orderItem = OrderItem.brandNew()
                .orderId(orderId)
                .product(product)
                .quantity(quantity)
                .build();
        assertWith(orderItem,
                o -> assertThat(o.id()).isNotNull(),
                o -> assertThat(o.orderId()).isEqualTo(orderId),
                o -> assertThat(o.productId()).isEqualTo(product.id()),
                o -> assertThat(o.productName()).isEqualTo(product.name()),
                o -> assertThat(o.price()).isEqualTo(product.price()),
                o -> assertThat(o.quantity()).isEqualTo(quantity),
                o -> assertThat(o.totalAmount()).isNotNull()
                );
    }

    @Test
    void shouldCreateExisting(){
        final var id = new OrderItemId();
        final var orderId = new OrderId();
        final var productId = new ProductId();
        final var productName = customFaker.valueObject().productName();
        final var price = customFaker.valueObject().money();
        final var quantity = customFaker.valueObject().quantity(1, 10);
        final var totalAmount = customFaker.valueObject().money();
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