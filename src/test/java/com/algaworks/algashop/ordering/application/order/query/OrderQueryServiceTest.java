package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.algaworks.algashop.ordering.application.order.query.OrderFilter.SortType.CANCELED_AT;
import static com.algaworks.algashop.ordering.application.order.query.OrderFilter.SortType.ORDER_STATUS;
import static com.algaworks.algashop.ordering.application.order.query.OrderFilter.SortType.PAID_AT;
import static com.algaworks.algashop.ordering.application.order.query.OrderFilter.SortType.PLACED_AT;
import static com.algaworks.algashop.ordering.application.order.query.OrderFilter.SortType.READY_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@SpringBootTest
@Transactional
class OrderQueryServiceTest extends AbstractApplicationTest {

    private final OrderQueryService queryService;
    private final Orders orders;
    private final Customers customers;

    private Customer customer;

    @Autowired
    OrderQueryServiceTest(final JdbcTemplate jdbcTemplate,
                          final OrderQueryService queryService,
                          final Orders orders,
                          final Customers customers) {
        super(jdbcTemplate);
        this.queryService = queryService;
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    void beforeEach(){
        CustomFaker.getInstance().reseed();
        customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
    }

    @Test
    void shouldFindById(){
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .buildExisting();
        orders.add(order);
        final var actual = queryService.findById(order.id().toString());
        assertThat(actual.getId())
                .isEqualTo(order.id().toString());
    }

    @Test
    void shouldFilterByPage(){
        final var ordersToInsert = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .buildExistingList(customFaker.number().numberBetween(5,10));
        ordersToInsert.forEach(orders::add);
        final var pageFilter = new OrderFilter(0, 3);
        final var actual = queryService.filter(pageFilter);
        final var isOdd = ordersToInsert.size() % pageFilter.getSize() != 0;
        int totalPages = ordersToInsert.size() / pageFilter.getSize() + (isOdd? 1 : 0);
        assertThat(actual.getTotalPages()).isEqualTo(totalPages);
        assertThat(actual.getTotalElements()).isEqualTo(ordersToInsert.size());
        assertThat(actual.getNumberOfElements()).isEqualTo(pageFilter.getSize());
    }

    @Test
    void shouldFilterByCustomerId(){
        final var customerToFilter = CustomerDataBuilder.builder().buildNew();
        customers.add(customerToFilter);
        final var ordersToInsert = OrderDataBuilder.builder()
                .withCustomerId(() -> customFaker.bool().bool() ? customerToFilter.id() :  customer.id())
                .buildExistingList(customFaker.number().numberBetween(5,10));
        ordersToInsert.forEach(orders::add);
        final var pageFilter = new OrderFilter();
        pageFilter.setCustomerId(customerToFilter.id().value());
        final var actual = queryService.filter(pageFilter);
        final var expectedOrderAmount =
                ordersToInsert.stream()
                        .filter(o -> o.customerId().equals(customerToFilter.id()))
                        .count();
        assertThat(actual.getTotalElements()).isEqualTo(expectedOrderAmount);
    }

    @Test
    void shouldFilterByOrderStatus(){
        final var ordersToInsert = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .buildExistingList(customFaker.number().numberBetween(5,10));
        ordersToInsert.forEach(orders::add);
        final var orderStatus = List.of(ordersToInsert.toArray(new Order[0]))
                .get(customFaker.number().numberBetween(0, ordersToInsert.size())).orderStatus()
                .toString();
        final var pageFilter = new OrderFilter();
        pageFilter.setOrderStatus(orderStatus);
        final var actual = queryService.filter(pageFilter);
        final var expectedOrderAmount =
                ordersToInsert.stream()
                        .filter(o -> o.orderStatus().equals(OrderStatus.valueOf(orderStatus)))
                        .count();
        assertThat(actual.getTotalElements()).isEqualTo(expectedOrderAmount);
    }

    private static Stream<Arguments> shouldSortBy(){
        return Stream.of(
                Arguments.of(ASC,
                        PLACED_AT,
                        Comparator.comparing(OrderSummaryOutput::getPlacedAt)
                ),
                Arguments.of(
                        ASC,
                        ORDER_STATUS,
                        Comparator.comparing(OrderSummaryOutput::getOrderStatus)
                ),
                Arguments.of(
                        ASC,
                        PAID_AT,
                        Comparator.comparing(OrderSummaryOutput::getPaidAt)
                ),
                Arguments.of(
                        ASC,
                        READY_AT,
                        Comparator.comparing(OrderSummaryOutput::getReadyAt)
                ),
                Arguments.of(
                        ASC,
                        CANCELED_AT,
                        Comparator.comparing(OrderSummaryOutput::getCanceledAt)
                ),
                Arguments.of(DESC,
                        PLACED_AT,
                        Comparator.comparing(OrderSummaryOutput::getPlacedAt).reversed()
                ),
                Arguments.of(DESC,
                        ORDER_STATUS,
                        Comparator.comparing(OrderSummaryOutput::getOrderStatus).reversed()
                ),
                Arguments.of(DESC,
                        PAID_AT,
                        Comparator.comparing(OrderSummaryOutput::getPaidAt).reversed()
                ),
                Arguments.of(DESC,
                        READY_AT,
                        Comparator.comparing(OrderSummaryOutput::getReadyAt).reversed()
                ),
                Arguments.of(DESC,
                        CANCELED_AT,
                        Comparator.comparing(OrderSummaryOutput::getCanceledAt).reversed()
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldSortBy(final Sort.Direction direction,
                      final OrderFilter.SortType sortType,
                      final Comparator<OrderSummaryOutput> comparator) {
        final var ordersToInsert = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .buildExistingList(customFaker.number().numberBetween(5,10));
        ordersToInsert.forEach(orders::add);
        final var orderStatus = List.of(ordersToInsert.toArray(new Order[0]))
                .get(customFaker.number().numberBetween(0, ordersToInsert.size())).orderStatus()
                .toString();
        final var pageFilter = new OrderFilter(0, 10);
        pageFilter.setSortDirection(direction);
        pageFilter.setSortByProperty(sortType);
        pageFilter.setOrderStatus(orderStatus);
        final var actual = queryService.filter(pageFilter);
        assertThat(actual.getContent()).isSortedAccordingTo(comparator);
    }

}