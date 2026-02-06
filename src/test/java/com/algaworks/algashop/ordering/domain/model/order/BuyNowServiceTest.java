package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;

class BuyNowServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private BuyNowService service;

    @BeforeEach
    void setUp() {
        service = new BuyNowService();
    }

    @Test
    void givenValidArgumentsWhenBuyNowThenReturnOrder(){
        final var product = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var customerId = new CustomerId();
        final var billing = customFaker.order().billing();
        final var shipping = customFaker.order().shipping();
        final var quantity = customFaker.common().quantity();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        final var actual = service.buyNow(
                product,
                customerId,
                billing,
                shipping,
                quantity,
                paymentMethod
        );
        final var expectedOrderAmount = shipping.cost().add(product.price().multiply(quantity));
        assertWith(actual,
                o -> assertThat(o.customerId()).isEqualTo(customerId),
                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.totalAmount()).isEqualTo(expectedOrderAmount),
                o -> assertThat(o.totalItems()).isEqualTo(quantity),
                o -> assertThat(o.isPlaced()).isTrue(),
                o -> assertThat(o.placedAt()).isNotNull(),
                o -> assertThat(o.paidAt()).isNull(),
                o -> assertThat(o.canceledAt()).isNull(),
                o -> assertThat(o.readyAt()).isNull()
        );
        assertThat(actual.items()).hasSize(1);
    }

    private static final List<Arguments> givenInvalidArgsWhenBuyNowThenThrowException = List.of(
            Arguments.of(
                    ProductDataBuilder.builder().withInStock(() -> true).build(),
                    new CustomerId(),
                    customFaker.order().billing(),
                    customFaker.order().shipping(),
                    customFaker.common().quantity(),
                    null
            ),
            Arguments.of(
                    ProductDataBuilder.builder().withInStock(() -> true).build(),
                    new CustomerId(),
                    customFaker.order().billing(),
                    customFaker.order().shipping(),
                    null,
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ProductDataBuilder.builder().withInStock(() -> true).build(),
                    new CustomerId(),
                    customFaker.order().billing(),
                    null,
                    customFaker.common().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ProductDataBuilder.builder().withInStock(() -> true).build(),
                    new CustomerId(),
                    null,
                    customFaker.order().shipping(),
                    customFaker.common().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ProductDataBuilder.builder().withInStock(() -> true).build(),
                    null,
                    customFaker.order().billing(),
                    customFaker.order().shipping(),
                    customFaker.common().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    null,
                    new CustomerId(),
                    customFaker.order().billing(),
                    customFaker.order().shipping(),
                    customFaker.common().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            )
    );

    @FieldSource
    @ParameterizedTest
    void givenInvalidArgsWhenBuyNowThenThrowException(final Product product,
                                                      final CustomerId customerId,
                                                      final Billing billing,
                                                      final Shipping shipping,
                                                      final Quantity quantity,
                                                      final PaymentMethod paymentMethod){
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> service.buyNow(
                        product,
                        customerId,
                        billing,
                        shipping,
                        quantity,
                        paymentMethod
                ));
    }

    @Test
    void givenZeroQuantityWhenBuyNowThenThrowException(){
        final var product = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var customerId = new CustomerId();
        final var billing = customFaker.order().billing();
        final var shipping = customFaker.order().shipping();
        final var quantity = Quantity.ZERO;
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> service.buyNow(
                    product,
                    customerId,
                    billing,
                    shipping,
                    quantity,
                    paymentMethod
            ));
    }

    @Test
    void givenProductWithoutStockWhenBuyNowThenThrowException(){
        final var product = ProductDataBuilder.builder().withInStock(() -> false).build();
        final var customerId = new CustomerId();
        final var billing = customFaker.order().billing();
        final var shipping = customFaker.order().shipping();
        final var quantity = customFaker.common().quantity();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> service.buyNow(
                        product,
                        customerId,
                        billing,
                        shipping,
                        quantity,
                        paymentMethod
                ));
    }

}