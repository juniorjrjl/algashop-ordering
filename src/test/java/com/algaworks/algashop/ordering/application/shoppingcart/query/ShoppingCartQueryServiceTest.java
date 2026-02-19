package com.algaworks.algashop.ordering.application.shoppingcart.query;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFound;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class ShoppingCartQueryServiceTest extends AbstractApplicationTest {

    private final ShoppingCartQueryService queryService;
    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    private Customer customer;

    @Autowired
    ShoppingCartQueryServiceTest(final JdbcTemplate jdbcTemplate,
                                 final ShoppingCartQueryService queryService,
                                 final Customers customers,
                                 final ShoppingCarts shoppingCarts) {
        super(jdbcTemplate);
        this.queryService = queryService;
        this.customers = customers;
        this.shoppingCarts = shoppingCarts;
    }

    @BeforeEach
    void beforeEach(){
        CustomFaker.getInstance().reseed();
        customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
    }

    @Test
    void shouldFindById(){
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .build();
        shoppingCarts.add(shoppingCart);
        final var actual = queryService.findById(shoppingCart.id().value());
        assertThat(actual.getId()).isEqualTo(shoppingCart.id().value());
    }

    @Test
    void givenNoStoredShoppingCartWhenFindByIdThenThrowException(){
        assertThatExceptionOfType(ShoppingCartNotFound.class)
                .isThrownBy(() -> queryService.findById(UUID.randomUUID()));
    }

    @Test
    void shouldFindByCustomerId(){
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .build();
        shoppingCarts.add(shoppingCart);
        final var actual = queryService.findByCustomerId(customer.id().value());
        assertThat(actual.getId())
                .isEqualTo(shoppingCart.id().value());
    }

    @Test
    void givenNoStoredShoppingCartWhenFindByCustomerIdThenThrowException(){
        assertThatExceptionOfType(ShoppingCartNotFound.class)
                .isThrownBy(() -> queryService.findByCustomerId(UUID.randomUUID()));
    }

}