package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.PersistenceUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomersPersistenceProvider implements Customers {

    private final CustomerPersistenceEntityRepository repository;
    private final CustomerPersistenceEntityAssembler assembler;
    private final CustomerPersistenceEntityDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<Customer> ofId(final CustomerId customerId) {
        return repository.findById(customerId.value())
                .map(disassembler::toDomain);
    }

    @Override
    public boolean exists(final CustomerId customerId) {
        return repository.existsById(customerId.value());
    }

    @Override
    @Transactional
    public void add(final Customer aggregateRoot) {
        final var id = aggregateRoot.id().value();
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
    public Optional<Customer> ofEmail(final Email email) {
        return repository.findByEmail(email.value())
                .map(disassembler::toDomain);
    }

    @Override
    public boolean isEmailUnique(final Email email, final CustomerId exceptCustomerId) {
        return !repository.existsByEmailAndIdNot(email.value(), exceptCustomerId.value());
    }

    private void insert(final Customer aggregateRoot) {
        final var toInsert = assembler.toDomain(
                new CustomerPersistenceEntity(),
                aggregateRoot
        );
        repository.saveAndFlush(toInsert);
        PersistenceUtil.updateVersion(aggregateRoot, toInsert.getVersion());
    }

    private void update(final Customer aggregateRoot, final CustomerPersistenceEntity entity) {
        final var updated = assembler.toDomain(entity, aggregateRoot);
        entityManager.detach(updated);
        repository.saveAndFlush(updated);
        PersistenceUtil.updateVersion(aggregateRoot, entity.getVersion());
    }

}
