package com.algaworks.algashop.ordering.utility.databuilder.domain;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ShoppingCartDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<ShoppingCartId> id = ShoppingCartId::new;
    @With
    private Supplier<CustomerId> customerId = CustomerId::new;
    @With
    private Supplier<OffsetDateTime> createdAt = OffsetDateTime::now;
    @With
    private Supplier<Set<ShoppingCartItem>> items = () -> ShoppingCartItemDataBuilder
            .builder()
            .buildSet(customFaker.number().numberBetween(1, 10));

    public static ShoppingCartDataBuilder builder(){
        return new ShoppingCartDataBuilder();
    }

    public static ShoppingCartDataBuilder builder(ShoppingCart shoppingCart){
        final var id = shoppingCart.id();
        final var customerId = shoppingCart.customerId();
        final var createdAt = shoppingCart.createdAt();
        final var items = new HashSet<>(shoppingCart.items());
        return new ShoppingCartDataBuilder(
                () -> id,
                () -> customerId,
                () -> createdAt,
                () -> items
        );
    }

    public ShoppingCart build(){
        return ShoppingCart.existing()
                .id(id.get())
                .customerId(customerId.get())
                .createdAt(createdAt.get())
                .items(items.get())
                .build();
    }

}
