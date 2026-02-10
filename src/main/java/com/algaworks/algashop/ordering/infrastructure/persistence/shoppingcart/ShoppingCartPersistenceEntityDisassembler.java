package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassembler;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@AnnotateWith(NullMarked.class)
@Mapper(componentModel = SPRING, uses = EmbeddableDisassembler.class, injectionStrategy = CONSTRUCTOR)
public interface ShoppingCartPersistenceEntityDisassembler {

    @Mapping(target = "customerId", source = "entity.customer.id")
    ShoppingCart toDomain(@MappingTarget final ShoppingCart.ExistingOrderBuilder builder,
                          final ShoppingCartPersistenceEntity entity);

    @Mapping(target = "shoppingCartId", source = "entity.shoppingCart.id")
    ShoppingCartItem.ExistingShoppingCartItemBuilder toDomain(@MappingTarget final ShoppingCartItem.ExistingShoppingCartItemBuilder builder,
                                                              final ShoppingCartItemPersistenceEntity entity);

    default Set<ShoppingCartItem> toDomain(final Set<ShoppingCartItemPersistenceEntity> items) {
        if (items.isEmpty()) {
            return new HashSet<>();
        }
        return items.stream()
                .map(i -> toDomain(ShoppingCartItem.existing(), i).build())
                .collect(Collectors.toSet());
    }

    default ShoppingCart toDomain(final ShoppingCartPersistenceEntity entity) {
        return toDomain(ShoppingCart.existing(), entity);
    }

    ShoppingCartId toShoppingCartId(final UUID value);

    ShoppingCartItemId toShoppingCartItemId(final UUID value);

}
