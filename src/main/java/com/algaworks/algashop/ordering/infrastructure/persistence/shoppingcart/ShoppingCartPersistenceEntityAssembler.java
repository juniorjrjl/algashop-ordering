package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
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
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

@Mapper(componentModel = SPRING)
@AnnotateWith(NullMarked.class)
public abstract class ShoppingCartPersistenceEntityAssembler {

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

    @Mapping(target = "id", expression = "java(map(shoppingCart.id()))")
    @Mapping(target = "customer", expression = "java(getCustomerReference(shoppingCart.customerId()))")
    @Mapping(target = "totalAmount", expression = "java(getEmbeddableAssembler().map(shoppingCart.totalAmount()))")
    @Mapping(target = "totalItems", expression = "java(getEmbeddableAssembler().map(shoppingCart.totalItems()))")
    @Mapping(target = "createdAt", expression = "java(shoppingCart.createdAt())")
    @Mapping(target = "items", expression = "java(fromDomain(shoppingCart.items()))")
    @Mapping(target = "version", expression = "java(shoppingCart.version())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    public abstract ShoppingCartPersistenceEntity fromDomain(@MappingTarget final ShoppingCartPersistenceEntity entity,
                                                             final ShoppingCart shoppingCart);

    @AfterMapping
    public ShoppingCartPersistenceEntity itemsSetup(@MappingTarget final ShoppingCartPersistenceEntity entity,
                                                             final ShoppingCart shoppingCart){
        entity.addCartToItems();
        return entity;
    }

    public CustomerPersistenceEntity getCustomerReference(final CustomerId customerId){
        return getCustomerRepository().getReferenceById(getEmbeddableAssembler().map(customerId));
    }

    @IterableMapping(nullValueMappingStrategy = RETURN_DEFAULT)
    protected abstract Set<ShoppingCartItemPersistenceEntity> fromDomain(final Set<ShoppingCartItem> shoppingCartItems);

    @Mapping(target = "id", expression = "java(map(item.id()))")
    @Mapping(target = "productId", expression = "java(getEmbeddableAssembler().map(item.productId()))")
    @Mapping(target = "name", expression = "java(getEmbeddableAssembler().map(item.name()))")
    @Mapping(target = "price", expression = "java(getEmbeddableAssembler().map(item.price()))")
    @Mapping(target = "quantity", expression = "java(getEmbeddableAssembler().map(item.quantity()))")
    @Mapping(target = "totalAmount", expression = "java(getEmbeddableAssembler().map(item.totalAmount()))")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "shoppingCart", ignore = true)
    protected abstract ShoppingCartItemPersistenceEntity fromDomain(final ShoppingCartItem item);

    protected UUID map(final ShoppingCartId shoppingCartId) {
        return shoppingCartId.value();
    }

    protected UUID map(final ShoppingCartItemId shoppingCartItemId) {
        return shoppingCartItemId.value();
    }

}
