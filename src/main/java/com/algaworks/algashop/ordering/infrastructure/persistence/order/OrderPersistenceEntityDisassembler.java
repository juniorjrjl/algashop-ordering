package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderItem;
import com.algaworks.algashop.ordering.domain.model.order.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassembler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@AnnotateWith(NullMarked.class)
@Mapper(componentModel = SPRING, uses = EmbeddableDisassembler.class, injectionStrategy = CONSTRUCTOR)
public interface OrderPersistenceEntityDisassembler {

    @Mapping(target = "customerId", source = "customer.id")
    Order.ExistingOrderBuilder toDomain(@MappingTarget final Order.ExistingOrderBuilder builder,
                                        final OrderPersistenceEntity aggregateRoot);

    OrderItem.ExistingOrderItemBuilder toDomain(@MappingTarget final OrderItem.ExistingOrderItemBuilder builder,
                                                final OrderItemPersistenceEntity item);

    default OrderId toOrderId(final Long value){
        return new OrderId(value);
    }

    default OrderItemId toOrderItemId(final Long value){
        return new OrderItemId(value);
    }

    default Set<OrderItem> toDomain(final Set<OrderItemPersistenceEntity> items){
        if (items.isEmpty()) {
            return new HashSet<>();
        }
        return items.stream()
                .map(i -> toDomain(OrderItem.existing(), i).build())
                .collect(Collectors.toSet());
    }

    default Order toDomain(final OrderPersistenceEntity aggregateRoot){
        return toDomain(Order.existing(), aggregateRoot).build();
    }

    @Nullable
    default PaymentMethod mapPaymentMethod(@Nullable final String method) {
        return isNull(method) ? null : PaymentMethod.valueOf(method);
    }

    default OrderStatus mapOrderStatus(final String status) {
        return OrderStatus.valueOf(status);
    }

    @Mapping(target = "fullName", expression = "java(embeddableDisassembler.toFullName(recipient.getFirstName(), recipient.getLastName()))")
    Recipient toRecipient(final RecipientEmbeddable recipient);

    Shipping toShipping(final ShippingEmbeddable shipping);

    @Mapping(target = "fullName", expression = "java(embeddableDisassembler.toFullName(billing.getFirstName(), billing.getLastName()))")
    Billing toBilling(final BillingEmbeddable billing);

}
