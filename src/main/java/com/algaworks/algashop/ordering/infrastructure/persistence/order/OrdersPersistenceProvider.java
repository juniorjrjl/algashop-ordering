package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.PersistenceUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
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
        aggregateRoot.clearDomainEvents();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<Order> placedByCustomerInYear(final CustomerId customerId, final Year year) {
        final var entities =  repository.placedByCustomerInYear(
                customerId.value(),
                year.getValue()
        );
        return entities.stream().map(disassembler::toDomain).toList();
    }

    @Override
    public Long salesQuantityByCustomerInYear(final CustomerId customerId, final Year year) {
        return repository.salesQuantityByCustomerInYear(customerId.value(), year.getValue());
    }

    @Override
    public Money totalSoldForCustomer(final CustomerId customerId) {
        final var value =  repository.totalSoldForCustomer(customerId.value());
        return new Money(value);
    }

    private void insert(final Order aggregateRoot) {
        final var toInsert = assembler.fromDomain(
                new OrderPersistenceEntity(),
                aggregateRoot
        );
        repository.saveAndFlush(toInsert);
        PersistenceUtil.updateVersion(aggregateRoot, toInsert.getVersion());
    }

    private void update(final Order aggregateRoot, final OrderPersistenceEntity entity) {
        final var updated = assembler.fromDomain(entity, aggregateRoot);
        entityManager.detach(updated);
        repository.saveAndFlush(updated);
        PersistenceUtil.updateVersion(aggregateRoot, entity.getVersion());
    }

}
