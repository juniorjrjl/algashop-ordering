package com.algaworks.algashop.ordering.application.shoppingcart.query;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@IntegrationTest
@SpringBootTest
@Transactional
@ExtendWith(PostgreSQLTestContainerExtension.class)
class ShoppingCartQueryServiceTest {

    private final ShoppingCartQueryService queryService;
    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    private Customer customer;

    @Autowired
    ShoppingCartQueryServiceTest(final ShoppingCartQueryService queryService,
                                 final Customers customers,
                                 final ShoppingCarts shoppingCarts) {
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
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
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
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> queryService.findByCustomerId(UUID.randomUUID()));
    }

}