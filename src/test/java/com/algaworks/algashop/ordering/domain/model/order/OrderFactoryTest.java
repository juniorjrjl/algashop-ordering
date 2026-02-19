package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderFactoryTest {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
    }

    @Test
    void shouldGenerateFilledOrderThatCanBePlaced(){
        final var customerId = new CustomerId();
        final var shipping = customFaker.order().shipping();
        final var billing = customFaker.order().billing();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity = customFaker.common().quantity();
        final var order = OrderFactory.filled(
                customerId,
                shipping,
                billing,
                paymentMethod,
                product,
                quantity
        );
        assertWith(order,
                o -> assertThat(o.customerId()).isEqualTo(customerId),
                o -> assertThat(o.shipping()).isEqualTo(shipping),
                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.paymentMethod()).isEqualTo(paymentMethod),
                o -> assertThat(o.isDraft()).isTrue(),
                o -> assertThat(o.items()).hasSize(1)
                );
        order.place();

        assertThat(order.isPlaced()).isTrue();
    }

}