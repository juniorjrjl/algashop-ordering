package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.utility.PersistenceUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartsPersistenceProvider implements ShoppingCarts {

    private final ShoppingCartPersistenceEntityDisassembler disassembler;
    private final ShoppingCartPersistenceEntityAssembler assembler;
    private final ShoppingCartPersistenceEntityRepository repository;
    private final EntityManager entityManager;

    @Override
    public Optional<ShoppingCart> ofCustomer(final CustomerId customerId) {
        return repository.findByCustomer_Id(customerId.value())
                .map(disassembler::toDomain);
    }

    @Override
    public void remove(final ShoppingCartId shoppingCartId) {
        this.repository.deleteById(shoppingCartId.value());
    }

    @Override
    public void remove(final ShoppingCart aggregate) {
        this.remove(aggregate.id());
    }

    @Override
    public Optional<ShoppingCart> ofId(final ShoppingCartId shoppingCartId) {
        return repository.findById(shoppingCartId.value())
                .map(disassembler::toDomain);
    }

    @Override
    public boolean exists(final ShoppingCartId shoppingCartId) {
        return repository.existsById(shoppingCartId.value());
    }

    @Transactional
    @Override
    public void add(final ShoppingCart aggregateRoot) {
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

    private void insert(final ShoppingCart aggregateRoot) {
        final var toInsert = assembler.fromDomain(
                new ShoppingCartPersistenceEntity(),
                aggregateRoot
        );
        repository.saveAndFlush(toInsert);
        PersistenceUtil.updateVersion(aggregateRoot, toInsert.getVersion());
    }

    private void update(final ShoppingCart aggregateRoot, final ShoppingCartPersistenceEntity entity) {
        final var updated = assembler.fromDomain(entity, aggregateRoot);
        entityManager.detach(updated);
        repository.saveAndFlush(updated);
        PersistenceUtil.updateVersion(aggregateRoot, entity.getVersion());
    }

}
