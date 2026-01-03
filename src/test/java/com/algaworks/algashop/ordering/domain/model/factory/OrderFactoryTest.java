package com.algaworks.algashop.ordering.domain.model.factory;

import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.BillingDataBuilder;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.ProductDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderFactoryTest {

    private final CustomFaker customFaker = new CustomFaker();

    @Test
    void shouldGenerateFilledOrderThatCanBePlaced(){
        final var customerId = new CustomerId();
        final var shipping = customFaker.valueObject().shipping();
        final var billing = BillingDataBuilder.builder().build();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var quantity = customFaker.valueObject().quantity();
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