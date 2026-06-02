package com.algaworks.algashop.ordering.application.customer.loyaltypoints;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CantAddLoyaltyPointsOrderIsNotReady;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderItemDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints.BASE_POINTS;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.READY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
class CustomerLoyaltyPointsApplicationServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final CustomerLoyaltyPointsApplicationService service;
    private final Customers customers;
    private final Orders orders;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    public CustomerLoyaltyPointsApplicationServiceTest(final CustomerLoyaltyPointsApplicationService service,
                                                       final Customers customers,
                                                       final Orders orders) {
        this.service = service;
        this.customers = customers;
        this.orders = orders;
    }

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
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
    void shouldAddLoyaltyPoints() {
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var product = ProductDataBuilder.builder()
                .withPrice(() -> customFaker.common().money(1000, 9999))
                .build();
        final var orderItems = OrderItemDataBuilder.builder()
                .withQuantity(() -> customFaker.common().quantity(1, 3))
                .withProduct(() -> product)
                .buildExistingList(3);
        final var order = OrderDataBuilder.builder(Order.draft(customer.id()))
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .withItems(() -> orderItems)
                .withOrderStatus(() -> READY)
                .buildExisting();
        orders.add(order);
        service.addLoyaltyPoints(customer.id().value(), order.id().toString());
        final var result = order.totalAmount().divide(new Money("1000"));
        final var expected = new LoyaltyPoints(result.value().intValue() * BASE_POINTS.value());
        final var actual = customers.ofId(customer.id()).orElseThrow();
        assertThat(actual.loyaltyPoints()).isEqualTo(expected);
    }

    @Test
    void givenNonStoredCustomerWhenAddLoyaltyPointsThenThrowException(){
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.addLoyaltyPoints(UUID.randomUUID(), customFaker.lorem().word()));
    }

    @Test
    void givenNonStoredOrderWhenAddLoyaltyPointsThenThrowException(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> service.addLoyaltyPoints(customer.id().value(), new OrderId().toString()));
    }

    @Test
    void givenArchivedCustomerWhenAddLoyaltyPointsThenThrowException() {
        final var customer = CustomerDataBuilder.builder().buildNew();
        customer.archive();
        customers.add(customer);
        final var product = ProductDataBuilder.builder()
                .withPrice(() -> customFaker.common().money(1000, 9999))
                .build();
        final var orderItems = OrderItemDataBuilder.builder()
                .withQuantity(() -> customFaker.common().quantity(1, 3))
                .withProduct(() -> product)
                .buildExistingList(3);
        final var order = OrderDataBuilder.builder(Order.draft(customer.id()))
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .withItems(() -> orderItems)
                .withOrderStatus(() -> READY)
                .buildExisting();
        orders.add(order);
        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> service.addLoyaltyPoints(customer.id().value(), order.id().toString()));
    }

    @Test
    void givenOrderNotBelongsToCustomerWhenAddLoyaltyPointsThenThrowException() {
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var customerWithoutOrder = CustomerDataBuilder.builder().buildNew();
        customers.add(customerWithoutOrder);
        final var product = ProductDataBuilder.builder()
                .withPrice(() -> customFaker.common().money(1000, 9999))
                .build();
        final var orderItems = OrderItemDataBuilder.builder()
                .withQuantity(() -> customFaker.common().quantity(1, 3))
                .withProduct(() -> product)
                .buildExistingList(3);
        final var order = OrderDataBuilder.builder(Order.draft(customer.id()))
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .withItems(() -> orderItems)
                .withOrderStatus(() -> READY)
                .buildExisting();
        orders.add(order);
        assertThatExceptionOfType(OrderNotBelongsToCustomerException.class)
                .isThrownBy(() -> service.addLoyaltyPoints(customerWithoutOrder.id().value(), order.id().toString()));
    }

    @Test
    void givenOrderInInvalidOrderStatusWhenAddLoyaltyPointsThenThrowException() {
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var product = ProductDataBuilder.builder()
                .withPrice(() -> customFaker.common().money(1000, 9999))
                .build();
        final var orderItems = OrderItemDataBuilder.builder()
                .withQuantity(() -> customFaker.common().quantity(1, 3))
                .withProduct(() -> product)
                .buildExistingList(3);
        final var order = OrderDataBuilder.builder(Order.draft(customer.id()))
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .withItems(() -> orderItems)
                .withOrderStatus(() -> customFaker.option(OrderStatus.class, READY))
                .buildExisting();
        orders.add(order);
        assertThatExceptionOfType(CantAddLoyaltyPointsOrderIsNotReady.class)
                .isThrownBy(() -> service.addLoyaltyPoints(customer.id().value(), order.id().toString()));
    }

    @Test
    void givenOrderWithTotalLessThanOneThousandWhenAddLoyaltyPointsThenAddNoneLoyaltyPoints() {
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var product = ProductDataBuilder.builder()
                .withPrice(() -> customFaker.common().money(1, 1000))
                .build();
        final var orderItems = OrderItemDataBuilder.builder()
                .withQuantity(() -> new Quantity(1))
                .withProduct(() -> product)
                .buildExistingList(1);
        final var shipping = customFaker.order()
                .shipping()
                .toBuilder()
                .cost(Money.ZERO)
                .build();
        final var order = OrderDataBuilder.builder(Order.draft(customer.id()))
                .withShipping(() -> customFaker.order().shipping())
                .withBilling(() -> customFaker.order().billing())
                .withPaymentMethod(() -> customFaker.options().option(PaymentMethod.class))
                .withItems(() -> orderItems)
                .withOrderStatus(() -> READY)
                .withShipping(() -> shipping)
                .buildExisting();
        orders.add(order);
        service.addLoyaltyPoints(customer.id().value(), order.id().toString());
        final var actual = customers.ofId(customer.id()).orElseThrow();
        assertThat(actual.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }

}