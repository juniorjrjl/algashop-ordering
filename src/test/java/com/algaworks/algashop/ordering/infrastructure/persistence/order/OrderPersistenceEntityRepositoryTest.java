package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.entity.OrderPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

class OrderPersistenceEntityRepositoryTest extends AbstractDBTest {

    private final OrderPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;

    @Autowired
    OrderPersistenceEntityRepositoryTest(final OrderPersistenceEntityRepository repository,
                                         final CustomerPersistenceEntityRepository customerRepository,
                                         final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
        this.customerEntity = CustomerPersistenceEntityDataBuilder.builder().withArchived(() -> false).build();
        this.customerEntity = customerRepository.save(customerEntity);
    }

    @Test
    void shouldPersistAndFind(){
        final var entity = OrderPersistenceEntityDataBuilder.builder()
                .withCustomer(() -> this.customerEntity)
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
                .withCustomer(() -> this.customerEntity)
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