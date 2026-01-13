package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatus.PAID;
import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatus.PLACED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@IntegrationTest
@DataJpaTest
@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssemblerImpl.class,
        OrderPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
class OrdersPersistenceProviderTest extends AbstractDBTest {

    private final OrdersPersistenceProvider persistenceProvider;
    private final OrderPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;

    @Autowired
    OrdersPersistenceProviderTest(final OrdersPersistenceProvider persistenceProvider,
                                  final OrderPersistenceEntityRepository repository,
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