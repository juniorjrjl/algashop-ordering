package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.JpaConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@IntegrationTest
@DataJpaTest
@Import({SpringDataAuditingConfig.class, JpaConfig.class})
public abstract class AbstractDBTest {

    protected CustomFaker customFaker = CustomFaker.getInstance();
    protected final JdbcTemplate jdbcTemplate;
    private final String[] TABLES = {"SHOPPING_CART_ITEMS", "SHOPPING_CARTS", "ORDER_ITEMS", "ORDERS", "CUSTOMERS"};

    public AbstractDBTest(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TABLES);
    }

}
