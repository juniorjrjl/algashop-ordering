package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrdersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Year;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.DRAFT;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.PAID;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.PLACED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssemblerImpl.class,
        OrderPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
class OrdersTest extends AbstractDBTest {

    private final Orders orders;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;

    @Autowired
    OrdersTest(final Orders orders,
               final CustomerPersistenceEntityRepository customerRepository,
               final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.orders = orders;
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
        this.customerEntity = CustomerPersistenceEntityDataBuilder.builder().withArchived(() -> false).build();
        this.customerEntity = customerRepository.save(customerEntity);
    }

    @Test
    void shouldPersistAndFind(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                .buildExisting();
        orders.add(order);
        final var optional = orders.ofId(order.id());
        assertThat(optional).isPresent();
        final var actual = optional.get();
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(order);
    }

    @Test
    void shouldUpdateExistingOrder(){
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                .withOrderStatus(() -> PLACED)
                .buildExisting();
        orders.add(order);
        final var storedOrder = orders.ofId(order.id()).orElseThrow();
        storedOrder.markAsPaid();
        orders.add(storedOrder);
        final var actual = orders.ofId(storedOrder.id()).orElseThrow();
        assertThat(actual.isPaid()).isTrue();
    }

    @Test
    void shouldNotAllowStaleUpdates(){
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                .withOrderStatus(() -> PLACED)
                .withPaidAt(() -> null)
                .withCanceledAt(() -> null)
                .buildExisting();
        orders.add(order);

        final var firstSearch = orders.ofId(order.id()).orElseThrow();
        final var secondSearch = orders.ofId(order.id()).orElseThrow();

        firstSearch.markAsPaid();
        orders.add(firstSearch);

        secondSearch.cancel();
        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> orders.add(secondSearch));

        final var storedOrder = orders.ofId(order.id()).orElseThrow();
        assertThat(storedOrder.canceledAt()).isNull();
        assertThat(storedOrder.paidAt()).isNotNull();
    }

    @Test
    void shouldCountExistingOrders(){
        assertThat(orders.count()).isZero();
        final var toInsert = Stream.generate(() -> OrderDataBuilder.builder()
                        .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                        .buildExisting())
                .limit(customFaker.number().numberBetween(1, 10))
                .collect(Collectors.toSet());
        toInsert.forEach(orders::add);
        assertThat(orders.count()).isEqualTo(toInsert.size());
    }

    @Test
    void shouldReturnIfOrdersExist(){
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                .buildExisting();
        orders.add(order);
        assertThat(orders.exists(order.id())).isTrue();
    }

    @Test
    void shouldReturnIfOrdersNotExist(){
        assertThat(orders.exists(new OrderId())).isFalse();
    }

    @Test
    void shouldListExistingOrdersByYear(){
        final Supplier<Order> orderSupplier = () -> OrderDataBuilder.builder()
                .withOrderStatus(() -> DRAFT)
                .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                .buildExisting();
        final var ordersAmount = customFaker.number().numberBetween(1, 10);
        final var yearOrders = Stream.generate(orderSupplier)
                .limit(ordersAmount)
                .toList();
        yearOrders.forEach(Order::place);
        yearOrders.forEach(orders::add);
        assertThat(yearOrders).isNotEmpty().hasSize(ordersAmount);
    }

    @Test
    void shouldReturnSalesAmountByCustomer(){
        final Supplier<Order> orderSupplier = () -> OrderDataBuilder.builder()
                .withOrderStatus(() -> PAID)
                .withCanceledAt(() -> null)
                .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                .buildExisting();
        final var ordersAmount = customFaker.number().numberBetween(1, 10);
        final var payedOrders = Stream.generate(orderSupplier)
                .limit(ordersAmount)
                .toList();
        payedOrders.forEach(orders::add);
        final var salesAmount = payedOrders.stream()
                .map(Order::totalAmount)
                .reduce(Money.ZERO, Money::add);
        assertThat(orders.totalSoldForCustomer(new CustomerId(customerEntity.getId()))).isEqualTo(salesAmount);
    }

    @Test
    void givenCustomerWithoutOrdersShouldReturnZeroMoney(){
        assertThat(orders.totalSoldForCustomer(new CustomerId())).isEqualTo(Money.ZERO);
    }

    @Test
    void shouldReturnSalesQuantityByCustomer(){
        final Supplier<Order> orderSupplier = () -> OrderDataBuilder.builder()
                .withOrderStatus(() -> PAID)
                .withCanceledAt(() -> null)
                .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                .buildExisting();
        final var ordersAmount = customFaker.number().numberBetween(1, 10);
        final var payedOrders = Stream.generate(orderSupplier)
                .limit(ordersAmount)
                .toList();
        payedOrders.forEach(orders::add);
        assertThat(orders.salesQuantityByCustomerInYear(new CustomerId(customerEntity.getId()), Year.now()))
                .isEqualTo(payedOrders.size());
    }

    @Test
    void givenCustomerWithoutOrdersShouldReturnZeroQuantity(){
        final Supplier<Order> orderSupplier = () -> OrderDataBuilder.builder()
                .withOrderStatus(() -> PAID)
                .withCanceledAt(() -> null)
                .withCustomerId(() -> new CustomerId(customerEntity.getId()))
                .buildExisting();
        final var ordersAmount = customFaker.number().numberBetween(1, 10);
        final var payedOrders = Stream.generate(orderSupplier)
                .limit(ordersAmount)
                .toList();
        payedOrders.forEach(orders::add);
        assertThat(orders.salesQuantityByCustomerInYear(
                new CustomerId(customerEntity.getId()),
                Year.now().minusYears(1)
        )).isZero();
    }

}
