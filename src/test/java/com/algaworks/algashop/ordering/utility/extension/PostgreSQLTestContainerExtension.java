package com.algaworks.algashop.ordering.utility.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.lang.reflect.Modifier;
import java.time.Duration;

public class PostgreSQLTestContainerExtension implements BeforeAllCallback, BeforeEachCallback {

    private static final PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:18.3-alpine")
                    .withCommand("postgres", "-c", "max_connections=500")
                    .withStartupTimeout(Duration.ofSeconds(60))
                    .waitingFor(Wait.forListeningPort());

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        postgreSQLContainer.start();
        Class<?> testClass = context.getRequiredTestClass();
        for (final var field : testClass.getDeclaredFields()) {


            if (Modifier.isStatic(field.getModifiers()) &&
                    field.getType().equals(PostgreSQLContainer.class) &&
                    field.isAnnotationPresent(PGContainer.class)) {

                field.setAccessible(true);
                field.set(null, postgreSQLContainer);
            }
        }
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final var dataSource = SpringExtension.getApplicationContext(context).getBean(DataSource.class);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

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
