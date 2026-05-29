package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.DBTestContainer;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(DBTestContainer.class)
class CustomerPersistenceEntityRepositoryTest extends AbstractDBTest {

    private final CustomerPersistenceEntityRepository repository;

    @Autowired
    CustomerPersistenceEntityRepositoryTest(final JdbcTemplate jdbcTemplate,
                                            final CustomerPersistenceEntityRepository repository) {
        super(jdbcTemplate);
        this.repository = repository;
    }

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
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