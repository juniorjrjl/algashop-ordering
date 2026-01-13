package com.algaworks.algashop.ordering.utility.databuilder.entity;

import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ShoppingCartItemPersistenceEntityDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<UUID> id = IdGenerator::generateUUID;
    @With
    private Supplier<UUID> productId  = IdGenerator::generateUUID;
    @With
    private Supplier<String> name = () ->customFaker.food().vegetable();
    @With
    private Supplier<BigDecimal> price =
            () -> new BigDecimal(Double.toString(customFaker.number().randomDouble(2 ,1, 100)));
    @With
    private Supplier<Integer> quantity = () -> customFaker.number().randomDigit();
    @With
    private Supplier<Boolean> available = () -> customFaker.bool().bool();

    public static ShoppingCartItemPersistenceEntityDataBuilder builder() {
        return new ShoppingCartItemPersistenceEntityDataBuilder();
    }

    public ShoppingCartItemPersistenceEntity build() {
        final var genQuantity = quantity.get();
        final var genPrice = price.get();
        return new ShoppingCartItemPersistenceEntity(
                id.get(),
                productId.get(),
                name.get(),
                genPrice,
                genQuantity,
                genPrice.multiply(new BigDecimal(genQuantity.toString())),
                available.get(),
                null
        );
    }

    public Set<ShoppingCartItemPersistenceEntity> buildList(final long amount){
        return Stream.generate(() -> ShoppingCartItemPersistenceEntityDataBuilder.builder().build())
                .limit(amount)
                .collect(Collectors.toSet());
    }

}
