package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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

}