package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.DBTestContainer;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.entity.ShoppingCartPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
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
@IntegrationTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(DBTestContainer.class)
class ShoppingCartPersistenceEntityRepositoryTest extends AbstractDBTest {

    private final ShoppingCartPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;

    @Autowired
    ShoppingCartPersistenceEntityRepositoryTest(final JdbcTemplate  jdbcTemplate,
                                                final ShoppingCartPersistenceEntityRepository repository,
                                                final CustomerPersistenceEntityRepository customerRepository) {
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
        final var entity = ShoppingCartPersistenceEntityDataBuilder.builder()
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
        final var entity = ShoppingCartPersistenceEntityDataBuilder.builder()
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