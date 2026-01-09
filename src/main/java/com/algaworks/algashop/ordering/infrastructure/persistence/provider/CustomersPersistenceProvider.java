package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.utility.PersistenceUtil;
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
    }

    @Override
    public long count() {
        return repository.count();
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

    /*@SneakyThrows
    private void updateVersion(final Customer aggregateRoot, final Long currentVersion) {
        final var version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, currentVersion);
        version.setAccessible(false);
    }*/

}
