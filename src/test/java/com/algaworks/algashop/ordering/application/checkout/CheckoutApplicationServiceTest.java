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
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartItemDataBuilder;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
class CheckoutApplicationServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final CheckoutApplicationService service;
    private final ShoppingCarts shoppingCarts;
    private final Orders orders;
    private final Customers customers;

    @MockitoBean
    private OriginAddressService originAddressService;
    @MockitoBean
    private ShippingCostService shippingCostService;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    private static Customer customer;

    @Autowired
    public CheckoutApplicationServiceTest(final CheckoutApplicationService service,
                                          final ShoppingCarts shoppingCarts,
                                          final Orders orders,
                                          final Customers customers) {
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

    @DynamicPropertySource
    public static void configurePropertySource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @Test
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
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.checkout(input));
    }

    private static Stream<Supplier<ShoppingCart>> givenInvalidShoppingCartWhenCheckoutThenThrowException(){
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