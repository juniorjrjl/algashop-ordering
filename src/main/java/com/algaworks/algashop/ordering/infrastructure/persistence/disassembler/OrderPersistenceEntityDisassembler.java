package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = EmbeddableDisassembler.class, injectionStrategy = CONSTRUCTOR)
public interface OrderPersistenceEntityDisassembler {

    Order.ExistingOrderBuilder toDomain(@MappingTarget final Order.ExistingOrderBuilder builder,
                                        final OrderPersistenceEntity aggregateRoot);

    OrderItem.ExistingOrderItemBuilder toDomain(@MappingTarget final OrderItem.ExistingOrderItemBuilder builder,
                                                final OrderItemPersistenceEntity item);

    default Set<OrderItem> toDomain(final Set<OrderItemPersistenceEntity> items){
        if (isNull(items) || items.isEmpty()) {
            return new HashSet<>();
        }
        return items.stream()
                .map(i -> toDomain(OrderItem.existing(), i).build())
                .collect(Collectors.toSet());
    }

    default Order toDomain(final OrderPersistenceEntity aggregateRoot){
        return toDomain(Order.existing(), aggregateRoot).build();
    }

}
