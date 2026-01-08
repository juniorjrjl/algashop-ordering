package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.domain.model.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.entity.OrderPersistenceEntityDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderPersistenceEntityRepositoryTest extends AbstractDBTest {

    private final OrderPersistenceEntityRepository repository;

    @Autowired
    OrderPersistenceEntityRepositoryTest(final OrderPersistenceEntityRepository repository,
                                         final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.repository = repository;
    }

    @Test
    void shouldPersistAndFind(){
        final var entity = OrderPersistenceEntityDataBuilder.builder()
                .build();
        repository.save(entity);
        assertThat(repository.existsById(entity.getId())).isTrue();
    }

    @Test
    void shouldCount(){
        assertThat(repository.count()).isZero();
    }

    @Test
    void shouldSetAuditingValues(){
        final var entity = OrderPersistenceEntityDataBuilder.builder()
                .withCreatedBy(() -> null)
                .withLastModifiedAt(() -> null)
                .withLastModifiedBy(() -> null)
                .build();
        final var actual = repository.save(entity);
        assertWith(actual,
                a -> assertThat(a.getCreatedBy()).isNotNull(),
                a -> assertThat(a.getLastModifiedAt()).isNotNull(),
                a -> assertThat(a.getLastModifiedBy()).isNotNull()
        );
    }


}