package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = SPRING)
public abstract class OrderPersistenceEntityAssembler {

    protected EmbeddableAssembler embeddableAssembler;
    protected CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    public void setEmbeddableAssembler(final EmbeddableAssembler embeddableAssembler,
                                       final CustomerPersistenceEntityRepository customerRepository) {
        this.embeddableAssembler = embeddableAssembler;
        this.customerRepository = customerRepository;
    }

    @Mapping(target = "id", expression = "java(embeddableAssembler.map(order.id()))")
    @Mapping(target = "customer", expression = "java(getCustomerReference(order.customerId()))")
    @Mapping(target = "totalAmount", expression = "java(embeddableAssembler.map(order.totalAmount()))")
    @Mapping(target = "totalItems", expression = "java(embeddableAssembler.map(order.totalItems()))")
    @Mapping(target = "orderStatus", expression = "java(embeddableAssembler.map(order.orderStatus()))")
    @Mapping(target = "paymentMethod", expression = "java(embeddableAssembler.map(order.paymentMethod()))")
    @Mapping(target = "placedAt", expression = "java(order.placedAt())")
    @Mapping(target = "paidAt", expression = "java(order.paidAt())")
    @Mapping(target = "canceledAt", expression = "java(order.canceledAt())")
    @Mapping(target = "readyAt", expression = "java(order.readyAt())")
    @Mapping(target = "version", expression = "java(order.version())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "billing", expression = "java(embeddableAssembler.map(order.billing()))")
    @Mapping(target = "shipping", expression = "java(embeddableAssembler.map(order.shipping()))")
    @Mapping(target = "items", expression = "java(fromDomain(order.items()))")
    public abstract OrderPersistenceEntity fromDomain(@MappingTarget final OrderPersistenceEntity entity,
                                               final Order order);

    @AfterMapping
    protected OrderPersistenceEntity itemsSetup(@MappingTarget final OrderPersistenceEntity entity,
                                              final Order order) {
        entity.addOrderToItems();
        return entity;
    }

    public CustomerPersistenceEntity getCustomerReference(final CustomerId customerId){
        return customerRepository.getReferenceById(embeddableAssembler.map(customerId));
    }

    @IterableMapping(nullValueMappingStrategy = RETURN_DEFAULT)
    abstract Set<OrderItemPersistenceEntity> fromDomain(final Set<OrderItem> items);

    @Mapping(target = "id", expression = "java(embeddableAssembler.map(item.id()))")
    @Mapping(target = "productId", expression = "java(embeddableAssembler.map(item.productId()))")
    @Mapping(target = "productName", expression = "java(embeddableAssembler.map(item.productName()))")
    @Mapping(target = "price", expression = "java(embeddableAssembler.map(item.price()))")
    @Mapping(target = "quantity", expression = "java(embeddableAssembler.map(item.quantity()))")
    @Mapping(target = "totalAmount", expression = "java(embeddableAssembler.map(item.totalAmount()))")
    @Mapping(target = "order", ignore = true)
    abstract OrderItemPersistenceEntity fromDomain(final OrderItem item);


}

