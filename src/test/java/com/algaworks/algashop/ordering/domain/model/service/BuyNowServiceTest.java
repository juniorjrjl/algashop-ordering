package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.exception.OutOfStockException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.BillingDataBuilder;
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
        final var billing = BillingDataBuilder.builder().build();
        final var shipping = customFaker.valueObject().shipping();
        final var quantity = customFaker.valueObject().quantity();
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
                    ProductDataBuilder.builder().build(),
                    new CustomerId(),
                    BillingDataBuilder.builder().build(),
                    customFaker.valueObject().shipping(),
                    customFaker.valueObject().quantity(),
                    null
            ),
            Arguments.of(
                    ProductDataBuilder.builder().build(),
                    new CustomerId(),
                    BillingDataBuilder.builder().build(),
                    customFaker.valueObject().shipping(),
                    null,
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ProductDataBuilder.builder().build(),
                    new CustomerId(),
                    BillingDataBuilder.builder().build(),
                    null,
                    customFaker.valueObject().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ProductDataBuilder.builder().build(),
                    new CustomerId(),
                    null,
                    customFaker.valueObject().shipping(),
                    customFaker.valueObject().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ProductDataBuilder.builder().build(),
                    null,
                    BillingDataBuilder.builder().build(),
                    customFaker.valueObject().shipping(),
                    customFaker.valueObject().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    null,
                    new CustomerId(),
                    BillingDataBuilder.builder().build(),
                    customFaker.valueObject().shipping(),
                    customFaker.valueObject().quantity(),
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
        final var billing = BillingDataBuilder.builder().build();
        final var shipping = customFaker.valueObject().shipping();
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
        final var billing = BillingDataBuilder.builder().build();
        final var shipping = customFaker.valueObject().shipping();
        final var quantity = customFaker.valueObject().quantity();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        assertThatExceptionOfType(OutOfStockException.class)
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