package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
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
        ShoppingCartsPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssemblerImpl.class,
        ShoppingCartPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
class ShoppingCartsPersistenceProviderTest extends AbstractDBTest {

    private final ShoppingCartsPersistenceProvider persistenceProvider;
    private final ShoppingCartPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;

    @Autowired
    ShoppingCartsPersistenceProviderTest(final ShoppingCartsPersistenceProvider persistenceProvider,
                                         final ShoppingCartPersistenceEntityRepository repository,
                                         final CustomerPersistenceEntityRepository customerRepository,
                                         final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.persistenceProvider = persistenceProvider;
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    void setup(){
        this.customerEntity = CustomerPersistenceEntityDataBuilder.builder().withArchived(() -> false).build();
        this.customerEntity = customerRepository.save(customerEntity);
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