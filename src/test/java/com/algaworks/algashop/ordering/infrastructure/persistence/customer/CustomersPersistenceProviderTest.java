package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@ActiveProfiles("test")
@IntegrationTest
@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssemblerImpl.class,
        CustomerPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
class CustomersPersistenceProviderTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final CustomersPersistenceProvider persistenceProvider;
    private final CustomerPersistenceEntityRepository repository;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    CustomersPersistenceProviderTest(final CustomersPersistenceProvider persistenceProvider,
                                     final CustomerPersistenceEntityRepository repository) {
        this.persistenceProvider = persistenceProvider;
        this.repository = repository;
    }

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
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
        final var customer = CustomerDataBuilder.builder()
                .withArchived(() -> false)
                .buildExisting();
        persistenceProvider.add(customer);

        final var stored = persistenceProvider.ofId(customer.id()).orElseThrow();
        final var loyaltyPointsToAdd = customFaker.customer().loyaltyPoints(10, 100);
        stored.addLoyaltyPoints(loyaltyPointsToAdd);
        persistenceProvider.add(stored);

        final var actual = repository.findById(customer.id().value())
                .orElseThrow();
        final var loyaltyPointsExcepted = customer.loyaltyPoints().add(loyaltyPointsToAdd);
        assertThat(actual.getLoyaltyPoints()).isEqualTo(loyaltyPointsExcepted.value());
    }

    @Test
    @Transactional(propagation = NOT_SUPPORTED)
    void shouldFindNotFailWhenNoTransaction(){
        final var customer = CustomerDataBuilder.builder().buildExisting();
        persistenceProvider.add(customer);
        assertThatNoException().isThrownBy(() -> repository.findById(customer.id().value()));
    }

}