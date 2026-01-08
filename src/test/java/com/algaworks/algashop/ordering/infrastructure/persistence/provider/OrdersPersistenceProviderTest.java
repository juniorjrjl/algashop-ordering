package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.domain.OrderDataBuilder;
import com.algaworks.algashop.ordering.domain.model.utility.tag.IntegrationTest;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
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
})
class OrdersPersistenceProviderTest extends AbstractDBTest {

    private final OrdersPersistenceProvider persistenceProvider;
    private final OrderPersistenceEntityRepository repository;

    @Autowired
    OrdersPersistenceProviderTest(final OrdersPersistenceProvider persistenceProvider,
                                  final OrderPersistenceEntityRepository repository,
                                  final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.persistenceProvider = persistenceProvider;
        this.repository = repository;
    }

    @Test
    void shouldUpdateAndKeepPersistenceEntityState(){
        final var order = OrderDataBuilder.builder()
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
        final var order = OrderDataBuilder.builder().buildExisting();
        persistenceProvider.add(order);
        assertThatNoException().isThrownBy(() -> repository.findById(order.id().value().toLong()));
    }

}