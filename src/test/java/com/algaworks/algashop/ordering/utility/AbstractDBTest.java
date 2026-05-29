package com.algaworks.algashop.ordering.utility;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;


public abstract class AbstractDBTest {

    protected final JdbcTemplate jdbcTemplate;

    protected AbstractDBTest(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.execute("""
            TRUNCATE TABLE SHOPPING_CART_ITEMS,
                SHOPPING_CARTS,
                ORDER_ITEMS,
                ORDERS,
                CUSTOMERS
            RESTART IDENTITY CASCADE
        """);
    }

}
