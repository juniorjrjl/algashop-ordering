package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassembler;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = EmbeddableDisassembler.class, injectionStrategy = CONSTRUCTOR)
public interface ShoppingCartPersistenceEntityDisassembler {

    @Mapping(target = "customerId", source = "entity.customer.id")
    ShoppingCart toDomain(@MappingTarget final ShoppingCart.ExistingOrderBuilder builder,
                          final ShoppingCartPersistenceEntity entity);

    @Mapping(target = "shoppingCartId", source = "entity.shoppingCart.id")
    ShoppingCartItem.ExistingShoppingCartItemBuilder toDomain(@MappingTarget final ShoppingCartItem.ExistingShoppingCartItemBuilder builder,
                                                              final ShoppingCartItemPersistenceEntity entity);

    default Set<ShoppingCartItem> toDomain(final Set<ShoppingCartItemPersistenceEntity> items) {
        if (isNull(items) || items.isEmpty()) {
            return new HashSet<>();
        }
        return items.stream()
                .map(i -> toDomain(ShoppingCartItem.existing(), i).build())
                .collect(Collectors.toSet());
    }

    default ShoppingCart toDomain(final ShoppingCartPersistenceEntity entity) {
        return toDomain(ShoppingCart.existing(), entity);
    }

}
