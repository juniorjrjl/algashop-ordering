package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderItem;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import org.jspecify.annotations.NonNull;
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

    @Mapping(target = "id", expression = "java(map(order.id()))")
    @Mapping(target = "customer", expression = "java(getCustomerReference(order.customerId()))")
    @Mapping(target = "totalAmount", expression = "java(embeddableAssembler.map(order.totalAmount()))")
    @Mapping(target = "totalItems", expression = "java(embeddableAssembler.map(order.totalItems()))")
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
    public abstract OrderPersistenceEntity fromDomain(@MappingTarget final OrderPersistenceEntity entity,
                                               final Order order);

    @AfterMapping
    protected OrderPersistenceEntity itemsSetup(@MappingTarget final @NonNull OrderPersistenceEntity entity,
                                              final Order order) {
        entity.addOrderToItems();
        return entity;
    }

    public CustomerPersistenceEntity getCustomerReference(final CustomerId customerId){
        return customerRepository.getReferenceById(embeddableAssembler.map(customerId));
    }

    @IterableMapping(nullValueMappingStrategy = RETURN_DEFAULT)
    abstract Set<OrderItemPersistenceEntity> fromDomain(final Set<OrderItem> items);

    @Mapping(target = "id", expression = "java(map(item.id()))")
    @Mapping(target = "productId", expression = "java(embeddableAssembler.map(item.productId()))")
    @Mapping(target = "productName", expression = "java(embeddableAssembler.map(item.productName()))")
    @Mapping(target = "price", expression = "java(embeddableAssembler.map(item.price()))")
    @Mapping(target = "quantity", expression = "java(embeddableAssembler.map(item.quantity()))")
    @Mapping(target = "totalAmount", expression = "java(embeddableAssembler.map(item.totalAmount()))")
    @Mapping(target = "order", ignore = true)
    abstract OrderItemPersistenceEntity fromDomain(final OrderItem item);

    @Mapping(target = "cost", expression = "java(embeddableAssembler.map(shipping.cost()))")
    @Mapping(target = "address", expression = "java(embeddableAssembler.map(shipping.address()))")
    abstract ShippingEmbeddable map(final Shipping shipping);

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    @Mapping(target = "document", expression = "java(embeddableAssembler.map(recipient.document()))")
    @Mapping(target = "phone", expression = "java(embeddableAssembler.map(recipient.phone()))")
    abstract RecipientEmbeddable map(final Recipient recipient);

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    @Mapping(target = "document", expression = "java(embeddableAssembler.map(billing.document()))")
    @Mapping(target = "phone", expression = "java(embeddableAssembler.map(billing.phone()))")
    @Mapping(target = "email", expression = "java(embeddableAssembler.map(billing.email()))")
    @Mapping(target = "address", expression = "java(embeddableAssembler.map(billing.address()))")
    abstract BillingEmbeddable map(final Billing billing);

    abstract String map(final OrderStatus status);

    abstract String map(final PaymentMethod method);

    protected Long map(final @NonNull OrderItemId orderItemId) {
        return orderItemId.value().toLong();
    }

    protected Long map(final @NonNull OrderId orderId) {
        return orderId.value().toLong();
    }

}

