package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.infrastructure.config.FreeShippingConfig;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.MockitoWithResetExtension;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.time.Year;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoWithResetExtension.class)
class BuyNowServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Mock
    private Orders orders;

    private BuyNowService service;

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
        final var freeShippingConfig = new FreeShippingConfig(100, 2, 2000);
        final var freeShippingSpecification = new CustomerHaveFreeShippingSpecification(
                orders,
                freeShippingConfig
        );
        service = new BuyNowService(freeShippingSpecification);
    }

    private static Stream<Arguments> givenValidArgumentsWhenBuyNowThenReturnOrder() {
        final var customerWithMoreThanOneHundredLoyaltyPoints =CustomerDataBuilder.builder()
                .withLoyaltyPoints(() -> customFaker.customer().loyaltyPoints(100, 2000))
                .buildExisting();
        final var customerWithLessThanOneHundredLoyaltyPoints =CustomerDataBuilder.builder()
                .withLoyaltyPoints(() -> customFaker.customer().loyaltyPoints(0, 100))
                .buildExisting();
        final Consumer<Orders> orderReturnMoreThanTwoYears = orders ->
                when(orders.salesQuantityByCustomerInYear(any(CustomerId.class), any(Year.class)))
                        .thenReturn(customFaker.number().numberBetween(2, Long.MAX_VALUE));
        final Consumer<Orders> orderReturnLessThanTwoYears = orders ->
                when(orders.salesQuantityByCustomerInYear(any(CustomerId.class), any(Year.class)))
                        .thenReturn(customFaker.number().numberBetween(0L, 2L));
        return Stream.of(
                Arguments.of(
                        customerWithMoreThanOneHundredLoyaltyPoints,
                        orderReturnLessThanTwoYears
                ),
                Arguments.of(
                        customerWithLessThanOneHundredLoyaltyPoints,
                        orderReturnMoreThanTwoYears
                )
        );
    }


    @ParameterizedTest
    @MethodSource
    void givenValidArgumentsWhenBuyNowThenReturnOrder(final Customer customer,
                                                      final Consumer<Orders> orderMockConsumer){
        final var product = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var billing = customFaker.order().billing();
        final var shipping = customFaker.order().shipping();
        final var quantity = customFaker.common().quantity();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        final var actual = service.buyNow(
                product,
                customer,
                billing,
                shipping,
                quantity,
                paymentMethod
        );
        orderMockConsumer.accept(orders);
        final var expectedOrderAmount = shipping.cost().add(product.price().multiply(quantity));
        assertWith(actual,
                o -> assertThat(o.customerId()).isEqualTo(customer.id()),
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

    private static Stream<Arguments> givenCustomerEligibleToFreeShippingWhenBuyNowThenReturnOrder(){
        final var customerWithMoreThanOneHundredLoyaltyPoints =CustomerDataBuilder.builder()
                .withLoyaltyPoints(() -> customFaker.customer().loyaltyPoints(100, 2000))
                .buildExisting();
        final var customerWithMoreThanTwoThousandLoyaltyPoints =CustomerDataBuilder.builder()
                .withLoyaltyPoints(() -> customFaker.customer().loyaltyPoints(2000, Integer.MAX_VALUE))
                .buildExisting();
        final Consumer<Orders> orderReturnMoreThanTwoYears = orders ->
                when(orders.salesQuantityByCustomerInYear(any(CustomerId.class), any(Year.class)))
                        .thenReturn(customFaker.number().numberBetween(2, Long.MAX_VALUE));
        final Consumer<Orders> orderReturnLessThanTwoYears = orders ->
                when(orders.salesQuantityByCustomerInYear(any(CustomerId.class), any(Year.class)))
                        .thenReturn(customFaker.number().numberBetween(0L, 2L));
        return Stream.of(
                Arguments.of(
                        customerWithMoreThanOneHundredLoyaltyPoints,
                        orderReturnMoreThanTwoYears
                ),
                Arguments.of(
                        customerWithMoreThanTwoThousandLoyaltyPoints,
                        orderReturnLessThanTwoYears
                )
        );
    }

    @MethodSource
    @ParameterizedTest
    void givenCustomerEligibleToFreeShippingWhenBuyNowThenReturnOrder(final Customer customer,
                                                                      final Consumer<Orders> orderMockConsumer){
        final var product = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var billing = customFaker.order().billing();
        final var shipping = customFaker.order().shipping();
        final var quantity = customFaker.common().quantity();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        orderMockConsumer.accept(orders);
        final var actual = service.buyNow(
                product,
                customer,
                billing,
                shipping,
                quantity,
                paymentMethod
        );
        final var expectedOrderAmount = product.price().multiply(quantity);
        assertWith(actual,
                o -> assertThat(o.customerId()).isEqualTo(customer.id()),
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
                    CustomerDataBuilder.builder().buildNew(),
                    customFaker.order().billing(),
                    customFaker.order().shipping(),
                    customFaker.common().quantity(),
                    null
            ),
            Arguments.of(
                    ProductDataBuilder.builder().withInStock(() -> true).build(),
                    CustomerDataBuilder.builder().buildNew(),
                    customFaker.order().billing(),
                    customFaker.order().shipping(),
                    null,
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ProductDataBuilder.builder().withInStock(() -> true).build(),
                    CustomerDataBuilder.builder().buildNew(),
                    customFaker.order().billing(),
                    null,
                    customFaker.common().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ProductDataBuilder.builder().withInStock(() -> true).build(),
                    CustomerDataBuilder.builder().buildNew(),
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
                    CustomerDataBuilder.builder().buildNew(),
                    customFaker.order().billing(),
                    customFaker.order().shipping(),
                    customFaker.common().quantity(),
                    customFaker.options().option(PaymentMethod.class)
            )
    );

    @FieldSource
    @ParameterizedTest
    void givenInvalidArgsWhenBuyNowThenThrowException(final Product product,
                                                      final Customer customer,
                                                      final Billing billing,
                                                      final Shipping shipping,
                                                      final Quantity quantity,
                                                      final PaymentMethod paymentMethod){
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> service.buyNow(
                        product,
                        customer,
                        billing,
                        shipping,
                        quantity,
                        paymentMethod
                ));
    }

    @Test
    void givenZeroQuantityWhenBuyNowThenThrowException(){
        final var product = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var customer = CustomerDataBuilder.builder().buildNew();
        final var billing = customFaker.order().billing();
        final var shipping = customFaker.order().shipping();
        final var quantity = Quantity.ZERO;
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> service.buyNow(
                    product,
                    customer,
                    billing,
                    shipping,
                    quantity,
                    paymentMethod
            ));
    }

    @Test
    void givenProductWithoutStockWhenBuyNowThenThrowException(){
        final var product = ProductDataBuilder.builder().withInStock(() -> false).build();
        final var customer = CustomerDataBuilder.builder().buildNew();
        final var billing = customFaker.order().billing();
        final var shipping = customFaker.order().shipping();
        final var quantity = customFaker.common().quantity();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> service.buyNow(
                        product,
                        customer,
                        billing,
                        shipping,
                        quantity,
                        paymentMethod
                ));
    }

}