package com.algaworks.algashop.ordering.infrastruct.persistence.repository;

import com.algaworks.algashop.ordering.domain.model.utility.databuilder.entity.OrderPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.domain.model.utility.tag.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@SpringBootTest
class OrderPersistenceEntityRepositoryTest {

    private final OrderPersistenceEntityRepository repository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderPersistenceEntityRepositoryTest(final OrderPersistenceEntityRepository repository,
                                                final JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "ORDERS");
    }

    @Test
    void shouldPersist(){
        final var entity = OrderPersistenceEntityDataBuilder.builder()
                .build();
        repository.save(entity);
        assertThat(repository.existsById(entity.getId())).isTrue();
    }

    @Test
    void shouldCount(){
        assertThat(repository.count()).isZero();
    }

}