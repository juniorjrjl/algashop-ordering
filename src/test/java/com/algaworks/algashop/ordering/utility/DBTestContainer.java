package com.algaworks.algashop.ordering.utility;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

@TestConfiguration
public class DBTestContainer {

    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:18.3-alpine")
            .withCommand("postgres", "-c", "max_connections=500")
            .withStartupTimeout(Duration.ofSeconds(60))
            .waitingFor(Wait.forListeningPort())
            .withReuse(true);

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgreSQLTestContainer() {
        return postgreSQLContainer;
    }

}
