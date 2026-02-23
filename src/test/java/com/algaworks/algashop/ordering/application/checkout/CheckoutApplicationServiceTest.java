package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFound;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartItemDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(PER_CLASS)
class CheckoutApplicationServiceTest extends AbstractApplicationTest {

    private final CheckoutApplicationService service;
    private final ShoppingCarts shoppingCarts;
    private final Orders orders;
    private final Customers customers;

    @MockitoBean
    private OriginAddressService originAddressService;
    @MockitoBean
    private ShippingCostService shippingCostService;

    private Customer customer;

    @Autowired
    public CheckoutApplicationServiceTest(final JdbcTemplate jdbcTemplate,
                                          final CheckoutApplicationService service,
                                          final ShoppingCarts shoppingCarts,
                                          final Orders orders,
                                          final Customers customers) {
        super(jdbcTemplate);
        this.service = service;
        this.shoppingCarts = shoppingCarts;
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
        customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
    }

    void shouldCheckout() {
        final var items = ShoppingCartItemDataBuilder.builder()
                .withAvailable(() -> true)
                .buildSet(3);
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .withItems(() -> items)
                .build();
        shoppingCarts.add(shoppingCart);
        final var input = customFaker.commonApplication()
                .checkout()
                .toBuilder()
                .shoppingCartId(shoppingCart.id().value())
                .build();
        final var originAddress = customFaker.common().address();
        when(originAddressService.originAddress()).thenReturn(originAddress);
        final var calculationResult = new ShippingCostService.CalculationResult(
                customFaker.common().money(),
                LocalDate.ofInstant(customFaker.timeAndDate().future(), UTC)
        );
        final var calculationRequest = new ShippingCostService.CalculationRequest(
                originAddress.zipCode(),
                new ZipCode(input.getShipping().getAddress().getZipCode())
        );
        when(shippingCostService.calculate(calculationRequest)).thenReturn(calculationResult);
        final var actual = service.checkout(input);
        assertThat(orders.ofId(new OrderId(actual))).isPresent();
        final var actualShoppingCart = shoppingCarts.ofId(shoppingCart.id())
                .orElseThrow();
        assertThat(actualShoppingCart.items()).isEmpty();
    }

    @Test
    void givenNonStoredShoppingCartWhenCheckoutThenThrowException(){
        final var input = customFaker.commonApplication()
                .checkout();
        assertThatExceptionOfType(ShoppingCartNotFound.class)
                .isThrownBy(() -> service.checkout(input));
    }

    private Stream<Supplier<ShoppingCart>> givenInvalidShoppingCartWhenCheckoutThenThrowException(){
        final Supplier<ShoppingCart> emptyShoppingCartSupplier = () -> {
            final var emptyShoppingCart = ShoppingCartDataBuilder.builder()
                    .withCustomerId(() -> customer.id())
                    .build();
            emptyShoppingCart.empty();
            return emptyShoppingCart;
        };

        final Supplier<ShoppingCart> unavailableItemShoppingCartSupplier = () ->{
            final var items = ShoppingCartItemDataBuilder.builder()
                    .withAvailable(() -> false)
                    .buildSet(1);
            return ShoppingCartDataBuilder.builder()
                    .withCustomerId(() -> customer.id())
                    .withItems(() -> items)
                    .build();
        };

        return Stream.of(
                emptyShoppingCartSupplier,
                unavailableItemShoppingCartSupplier
        );
    }

    @ParameterizedTest
    @MethodSource
    void givenInvalidShoppingCartWhenCheckoutThenThrowException(final Supplier<ShoppingCart> shoppingCartSupplier) {
        final var shoppingCart = shoppingCartSupplier.get();
        shoppingCarts.add(shoppingCart);
        final var input = customFaker.commonApplication()
                .checkout()
                .toBuilder()
                .shoppingCartId(shoppingCart.id().value())
                .build();
        final var originAddress = customFaker.common().address();
        when(originAddressService.originAddress()).thenReturn(originAddress);
        final var calculationResult = new ShippingCostService.CalculationResult(
                customFaker.common().money(),
                LocalDate.ofInstant(customFaker.timeAndDate().future(), UTC)
        );
        final var calculationRequest = new ShippingCostService.CalculationRequest(
                originAddress.zipCode(),
                new ZipCode(input.getShipping().getAddress().getZipCode())
        );
        when(shippingCostService.calculate(calculationRequest)).thenReturn(calculationResult);
        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> service.checkout(input));
    }
}