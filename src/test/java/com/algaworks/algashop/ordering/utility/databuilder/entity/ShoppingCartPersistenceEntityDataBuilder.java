package com.algaworks.algashop.ordering.utility.databuilder.entity;

import com.algaworks.algashop.ordering.domain.model.IdGenerator;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ShoppingCartPersistenceEntityDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<UUID> id = IdGenerator::generateUUID;
    @With
    private Supplier<CustomerPersistenceEntity> customer =
            () -> CustomerPersistenceEntityDataBuilder.builder()
                    .withArchived(() -> false)
                    .build();
    @With
    private Supplier<OffsetDateTime> createdAt  = OffsetDateTime::now;
    @With
    private Supplier<Set<ShoppingCartItemPersistenceEntity>> items =
            () -> ShoppingCartItemPersistenceEntityDataBuilder.builder()
                    .buildList(customFaker.number().randomDigit());
    @With
    private Supplier<@Nullable UUID> createdBy  = IdGenerator::generateUUID;
    @With
    private Supplier<@Nullable OffsetDateTime> lastModifiedAt  = OffsetDateTime::now;
    @With
    private Supplier<@Nullable UUID> lastModifiedBy = IdGenerator::generateUUID;

    public static ShoppingCartPersistenceEntityDataBuilder builder() {
        return new ShoppingCartPersistenceEntityDataBuilder();
    }

    public ShoppingCartPersistenceEntity build() {
        final var genItems = items.get();
        final var genTotalItems = genItems.stream()
                .map(ShoppingCartItemPersistenceEntity::getQuantity)
                .reduce(0, Integer::sum);
        final var genTotalAmount = genItems.stream()
                .map(ShoppingCartItemPersistenceEntity::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ShoppingCartPersistenceEntity(
                id.get(),
                customer.get(),
                genTotalAmount,
                genTotalItems,
                createdAt.get(),
                genItems,
                createdBy.get(),
                lastModifiedAt.get(),
                lastModifiedBy.get(),
                null
        );
    }

}
