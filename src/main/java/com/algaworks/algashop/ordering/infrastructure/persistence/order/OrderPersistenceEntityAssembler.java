package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderItem;
import com.algaworks.algashop.ordering.domain.model.order.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.mapstruct.AfterMapping;
import org.mapstruct.AnnotateWith;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@AnnotateWith(NullMarked.class)
@Mapper(componentModel = SPRING)
public abstract class OrderPersistenceEntityAssembler {

    @Nullable
    private EmbeddableAssembler embeddableAssembler;
    @Nullable
    private CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    public void setEmbeddableAssembler(final EmbeddableAssembler embeddableAssembler) {
        this.embeddableAssembler = embeddableAssembler;
    }

    @Autowired
    public void setCustomerRepository(final CustomerPersistenceEntityRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public EmbeddableAssembler getEmbeddableAssembler() {
        return requireNonNull(embeddableAssembler, "embeddableAssembler must be injected by Spring");
    }

    public CustomerPersistenceEntityRepository getCustomerRepository() {
        return requireNonNull(customerRepository, "customerRepository must be injected by Spring");
    }

    @Mapping(target = "id", expression = "java(map(order.id()))")
    @Mapping(target = "customer", expression = "java(getCustomerReference(order.customerId()))")
    @Mapping(target = "totalAmount", expression = "java(getEmbeddableAssembler().map(order.totalAmount()))")
    @Mapping(target = "totalItems", expression = "java(getEmbeddableAssembler().map(order.totalItems()))")
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
    @Mapping(target = "billing", expression = "java(mapNullable(order.billing()))")
    @Mapping(target = "shipping", expression = "java(mapNullable(order.shipping()))")
    @Mapping(target = "items", expression = "java(fromDomain(order.items()))")
    @Mapping(target = "events", ignore = true)
    public abstract OrderPersistenceEntity fromDomain(@MappingTarget final OrderPersistenceEntity entity,
                                               final Order order);

    @AfterMapping
    protected OrderPersistenceEntity itemsSetup(@MappingTarget final OrderPersistenceEntity entity,
                                              final Order order) {
        entity.addEvents(order.domainEvents());
        entity.addOrderToItems();
        return entity;
    }

    public CustomerPersistenceEntity getCustomerReference(final CustomerId customerId){
        return getCustomerRepository().getReferenceById(getEmbeddableAssembler().map(customerId));
    }

    @IterableMapping(nullValueMappingStrategy = RETURN_DEFAULT)
    abstract Set<OrderItemPersistenceEntity> fromDomain(final Set<OrderItem> items);

    @Mapping(target = "id", expression = "java(map(item.id()))")
    @Mapping(target = "productId", expression = "java(getEmbeddableAssembler().map(item.productId()))")
    @Mapping(target = "productName", expression = "java(getEmbeddableAssembler().map(item.productName()))")
    @Mapping(target = "price", expression = "java(getEmbeddableAssembler().map(item.price()))")
    @Mapping(target = "quantity", expression = "java(getEmbeddableAssembler().map(item.quantity()))")
    @Mapping(target = "totalAmount", expression = "java(getEmbeddableAssembler().map(item.totalAmount()))")
    @Mapping(target = "order", ignore = true)
    abstract OrderItemPersistenceEntity fromDomain(final OrderItem item);

    @Nullable
    protected ShippingEmbeddable mapNullable(@Nullable final Shipping shipping){
        return isNull(shipping) ? null : map(shipping);
    }

    @Mapping(target = "cost", expression = "java(getEmbeddableAssembler().map(shipping.cost()))")
    @Mapping(target = "address", expression = "java(getEmbeddableAssembler().map(shipping.address()))")
    abstract ShippingEmbeddable map(final Shipping shipping);

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    @Mapping(target = "document", expression = "java(getEmbeddableAssembler().map(recipient.document()))")
    @Mapping(target = "phone", expression = "java(getEmbeddableAssembler().map(recipient.phone()))")
    abstract RecipientEmbeddable map(final Recipient recipient);

    @Nullable
    protected BillingEmbeddable mapNullable(@Nullable final Billing billing){
        return isNull(billing) ? null : map(billing);
    }

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    @Mapping(target = "document", expression = "java(getEmbeddableAssembler().map(billing.document()))")
    @Mapping(target = "phone", expression = "java(getEmbeddableAssembler().map(billing.phone()))")
    @Mapping(target = "email", expression = "java(getEmbeddableAssembler().map(billing.email()))")
    @Mapping(target = "address", expression = "java(getEmbeddableAssembler().map(billing.address()))")
    abstract BillingEmbeddable map(final Billing billing);

    abstract String map(final OrderStatus status);

    abstract String map(@Nullable final PaymentMethod method);

    protected Long map(final OrderItemId orderItemId) {
        return orderItemId.value().toLong();
    }

    protected Long map(final OrderId orderId) {
        return orderId.value().toLong();
    }

}

