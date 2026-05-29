package com.algaworks.algashop.ordering.utility.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
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
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
        System.setProperty("spring.flyway.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.flyway.user", postgreSQLContainer.getUsername());
        System.setProperty("spring.flyway.password", postgreSQLContainer.getPassword());
        Runtime.getRuntime().addShutdownHook(new Thread(postgreSQLContainer::stop));
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

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.flyway.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.flyway.user=" + postgreSQLContainer.getUsername(),
                    "spring.flyway.password=" + postgreSQLContainer.getPassword()
            ).applyTo(context.getEnvironment());
        }
    }

}
