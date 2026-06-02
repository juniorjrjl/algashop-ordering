package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@ActiveProfiles("test")
@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
@Import({
        ShoppingCartsPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssemblerImpl.class,
        ShoppingCartPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
class ShoppingCartsPersistenceProviderTest {

    private final ShoppingCartsPersistenceProvider persistenceProvider;
    private final ShoppingCartPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    ShoppingCartsPersistenceProviderTest(final ShoppingCartsPersistenceProvider persistenceProvider,
                                         final ShoppingCartPersistenceEntityRepository repository,
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
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(this.customerEntity.getId()))
                .build();
        persistenceProvider.add(shoppingCart);

        final var stored = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();
        stored.empty();
        persistenceProvider.add(stored);

        final var actual = repository.findById(shoppingCart.id().value())
                .orElseThrow();
        assertThat(actual.getItems()).isEmpty();
        assertThat(actual.getCreatedBy()).isNotNull();
        assertThat(actual.getLastModifiedAt()).isNotNull();
        assertThat(actual.getLastModifiedBy()).isNotNull();
    }

    @Test
    @Transactional(propagation = NOT_SUPPORTED)
    void shouldFindNotFailWhenNoTransaction(){
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(this.customerEntity.getId()))
                .build();
        persistenceProvider.add(shoppingCart);
        assertThatNoException().isThrownBy(() -> repository.findById(shoppingCart.id().value()));
    }

}