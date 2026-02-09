package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@IntegrationTest
public abstract class AbstractApplicationTest {

    protected final CustomFaker customFaker = CustomFaker.getInstance();
    protected final JdbcTemplate jdbcTemplate;
    private final String[] TABLES = {"SHOPPING_CART_ITEMS", "SHOPPING_CARTS", "ORDER_ITEMS", "ORDERS", "CUSTOMERS"};

    public AbstractApplicationTest(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TABLES);
    }

}
