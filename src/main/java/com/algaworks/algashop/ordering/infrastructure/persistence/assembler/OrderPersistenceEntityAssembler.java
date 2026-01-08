package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = SPRING)
public interface OrderPersistenceEntityAssembler {

    @Mapping(target = "id", expression = "java(map(order.id()))")
    @Mapping(target = "customerId", expression = "java(map(order.customerId()))")
    @Mapping(target = "totalAmount", expression = "java(map(order.totalAmount()))")
    @Mapping(target = "totalItems", expression = "java(map(order.totalItems()))")
    @Mapping(target = "orderStatus", expression = "java(map(order.orderStatus()))")
    @Mapping(target = "paymentMethod", expression = "java(map(order.paymentMethod()))")
    @Mapping(target = "placedAt", expression = "java(order.placedAt())")
    @Mapping(target = "paidAt", expression = "java(order.paidAt())")
    @Mapping(target = "canceledAt", expression = "java(order.canceledAt())")
    @Mapping(target = "readyAt", expression = "java(order.readyAt())")
    @Mapping(target = "version", expression = "java(order.version())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "billing", expression = "java(map(order.billing()))")
    @Mapping(target = "shipping", expression = "java(map(order.shipping()))")
    @Mapping(target = "items", expression = "java(fromDomain(order.items()))")
    OrderPersistenceEntity fromDomain(@MappingTarget final OrderPersistenceEntity entity, final Order order);

    @AfterMapping
    default OrderPersistenceEntity itemsSetup(@MappingTarget final OrderPersistenceEntity entity,
                                              final Order order) {
        entity.addOrderToItems();
        return entity;
    }

    @IterableMapping(nullValueMappingStrategy = RETURN_DEFAULT)
    Set<OrderItemPersistenceEntity> fromDomain(final Set<OrderItem> items);

    @Mapping(target = "id", expression = "java(map(item.id()))")
    @Mapping(target = "productId", expression = "java(map(item.productId()))")
    @Mapping(target = "productName", expression = "java(map(item.productName()))")
    @Mapping(target = "price", expression = "java(map(item.price()))")
    @Mapping(target = "quantity", expression = "java(map(item.quantity()))")
    @Mapping(target = "totalAmount", expression = "java(map(item.totalAmount()))")
    @Mapping(target = "order", ignore = true)
    OrderItemPersistenceEntity fromDomain(final OrderItem item);

    ShippingEmbeddable map(final Shipping shipping);

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    RecipientEmbeddable map(final Recipient recipient);

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    BillingEmbeddable map(final Billing billing);

    String map(final OrderStatus status);

    String map(final PaymentMethod method);

    AddressEmbeddable map(final Address address);

    default String map(final ProductName productName) {
        return isNull(productName) ? null : productName.toString();
    }

    default String map(final Email email) {
        return isNull(email) ? null : email.value();
    }

    default String map(final Phone phone){
        return  isNull(phone) ? null : phone.value();
    }

    default String map(final Document document){
        return  isNull(document) ? null : document.value();
    }

    default String map(final ZipCode zipCode){
        return isNull(zipCode) ? null : zipCode.value();
    }

    default UUID map(final ProductId id){
        return isNull(id) ? null : id.value();
    }

    default Long map(final OrderItemId id){
        return isNull(id) ? null : id.value().toLong();
    }

    default Long map(final OrderId id) {
        return isNull(id) ? null : id.value().toLong();
    }

    default UUID map(final CustomerId id) {
        return isNull(id) ? null : id.value();
    }

    default BigDecimal map(final Money money) {
        return isNull(money) ? null : money.value();
    }

    default Integer map(final Quantity quantity) {
        return isNull(quantity) ? null : quantity.value();
    }

}

