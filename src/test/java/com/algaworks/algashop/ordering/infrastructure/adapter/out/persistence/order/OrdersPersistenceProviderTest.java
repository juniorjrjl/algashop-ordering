package com.algaworks.algashop.ordering.infrastructure.adapter.out.persistence.order;

import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.adapter.out.persistence.common.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.adapter.out.persistence.common.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.adapter.out.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.adapter.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static com.algaworks.algashop.ordering.core.domain.model.order.OrderStatus.PAID;
import static com.algaworks.algashop.ordering.core.domain.model.order.OrderStatus.PLACED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@ActiveProfiles("test")
@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssemblerImpl.class,
        OrderPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
class OrdersPersistenceProviderTest {

    private final OrdersPersistenceProvider persistenceProvider;
    private final OrderPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    OrdersPersistenceProviderTest(final OrdersPersistenceProvider persistenceProvider,
                                  final OrderPersistenceEntityRepository repository,
                                  final CustomerPersistenceEntityRepository customerRepository) {
        this.persistenceProvider = persistenceProvider;
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
        this.customerEntity = CustomerPersistenceEntityDataBuilder.builder().withArchived(() -> false).build();
        this.customerEntity = customerRepository.save(customerEntity);
    }

    @DynamicPropertySource
    public static void configurePropertySource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @Test
    void shouldUpdateAndKeepPersistenceEntityState(){
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(this.customerEntity.getId()))
                .withOrderStatus(() -> PLACED)
                .buildExisting();
        persistenceProvider.add(order);

        final var stored = persistenceProvider.ofId(order.id()).orElseThrow();
        stored.markAsPaid();
        persistenceProvider.add(stored);

        final var actual = repository.findById(order.id().value().toLong())
                .orElseThrow();
        assertThat(actual.getOrderStatus()).isEqualTo(PAID.name());
        assertThat(actual.getCreatedBy()).isNotNull();
        assertThat(actual.getLastModifiedAt()).isNotNull();
        assertThat(actual.getLastModifiedBy()).isNotNull();
    }

    @Test
    @Transactional(propagation = NOT_SUPPORTED)
    void shouldFindNotFailWhenNoTransaction(){
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(this.customerEntity.getId()))
                .buildExisting();
        persistenceProvider.add(order);
        assertThatNoException().isThrownBy(() -> repository.findById(order.id().value().toLong()));
    }

}