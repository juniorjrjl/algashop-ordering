package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class CustomerPersistenceEntityRepositoryTest extends AbstractDBTest {

    private final CustomerPersistenceEntityRepository repository;

    @Autowired
    CustomerPersistenceEntityRepositoryTest(final CustomerPersistenceEntityRepository repository,
                                            final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.repository = repository;
    }

    @Test
    void shouldPersistAndFind(){
        final var entity = CustomerPersistenceEntityDataBuilder.builder()
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
        final var entity = CustomerPersistenceEntityDataBuilder.builder()
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