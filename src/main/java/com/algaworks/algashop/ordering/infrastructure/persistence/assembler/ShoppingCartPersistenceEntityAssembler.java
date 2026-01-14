package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
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
public abstract class ShoppingCartPersistenceEntityAssembler {

    protected EmbeddableAssembler embeddableAssembler;
    protected CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    public void setEmbeddableAssembler(final EmbeddableAssembler embeddableAssembler,
                                       final CustomerPersistenceEntityRepository customerRepository) {
        this.embeddableAssembler = embeddableAssembler;
        this.customerRepository = customerRepository;
    }

    @Mapping(target = "id", expression = "java(embeddableAssembler.map(shoppingCart.id()))")
    @Mapping(target = "customer", expression = "java(getCustomerReference(shoppingCart.customerId()))")
    @Mapping(target = "totalAmount", expression = "java(embeddableAssembler.map(shoppingCart.totalAmount()))")
    @Mapping(target = "totalItems", expression = "java(embeddableAssembler.map(shoppingCart.totalItems()))")
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
        return customerRepository.getReferenceById(embeddableAssembler.map(customerId));
    }

    @IterableMapping(nullValueMappingStrategy = RETURN_DEFAULT)
    protected abstract Set<ShoppingCartItemPersistenceEntity> fromDomain(final Set<ShoppingCartItem> shoppingCartItems);

    @Mapping(target = "id", expression = "java(embeddableAssembler.map(item.id()))")
    @Mapping(target = "productId", expression = "java(embeddableAssembler.map(item.productId()))")
    @Mapping(target = "name", expression = "java(embeddableAssembler.map(item.name()))")
    @Mapping(target = "price", expression = "java(embeddableAssembler.map(item.price()))")
    @Mapping(target = "quantity", expression = "java(embeddableAssembler.map(item.quantity()))")
    @Mapping(target = "totalAmount", expression = "java(embeddableAssembler.map(item.totalAmount()))")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "shoppingCart", ignore = true)
    protected abstract ShoppingCartItemPersistenceEntity fromDomain(final ShoppingCartItem item);

}
