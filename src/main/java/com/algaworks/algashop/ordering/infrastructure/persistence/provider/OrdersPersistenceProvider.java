package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.repository.Orders;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersPersistenceProvider implements Orders {

    private final OrderPersistenceEntityRepository repository;
    private final OrderPersistenceEntityAssembler assembler;
    private final OrderPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<Order> ofId(final OrderId orderId) {
        return repository.findById(orderId.value().toLong())
                .map(disassembler::toDomain);
    }

    @Override
    public boolean exists(final OrderId orderId) {
        return repository.existsById(orderId.value().toLong());
    }

    @Override
    @Transactional
    public void add(final Order aggregateRoot) {
        final var id = aggregateRoot.id().value().toLong();
        repository.findById(id).ifPresentOrElse(
                e -> update(aggregateRoot, e),
                () -> insert(aggregateRoot)
        );
    }

    @Override
    public long count() {
        return repository.count();
    }

    private void insert(final Order aggregateRoot) {
        final var entity = assembler.fromDomain(
                new OrderPersistenceEntity(),
                aggregateRoot
        );
        repository.saveAndFlush(entity);
        updateVersion(aggregateRoot, entity.getVersion());
    }

    private void update(final Order aggregateRoot, final OrderPersistenceEntity entity) {
        final var updated = assembler.fromDomain(entity, aggregateRoot);
        entityManager.detach(updated);
        repository.saveAndFlush(updated);
        updateVersion(aggregateRoot, entity.getVersion());
    }

    @SneakyThrows
    private void updateVersion(final Order aggregateRoot, final Long currentVersion) {
        final var version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, currentVersion);
        version.setAccessible(false);
    }

}
