package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.domain.model.utility.tag.IntegrationTest;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@IntegrationTest
@DataJpaTest
@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssemblerImpl.class,
        CustomerPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
class CustomersPersistenceProviderTest extends AbstractDBTest {

    private final CustomersPersistenceProvider persistenceProvider;
    private final CustomerPersistenceEntityRepository repository;

    @Autowired
    CustomersPersistenceProviderTest(final CustomersPersistenceProvider persistenceProvider,
                                     final CustomerPersistenceEntityRepository repository,
                                     final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.persistenceProvider = persistenceProvider;
        this.repository = repository;
    }

    @Test
    void shouldUpdateAndKeepPersistenceEntityState(){
        final var customer = CustomerDataBuilder.builder()
                .withArchived(() -> false)
                .buildExisting();
        persistenceProvider.add(customer);

        final var stored = persistenceProvider.ofId(customer.id()).orElseThrow();
        final var loyaltyPointsToAdd = customFaker.valueObject().loyaltyPoints(10, 100);
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