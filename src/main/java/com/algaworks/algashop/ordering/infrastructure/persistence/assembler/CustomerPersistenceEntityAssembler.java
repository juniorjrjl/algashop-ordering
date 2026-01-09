package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = EmbeddableAssembler.class)
public abstract class CustomerPersistenceEntityAssembler {

    protected EmbeddableAssembler embeddableAssembler;

    @Autowired
    public void setEmbeddableAssembler(final EmbeddableAssembler embeddableAssembler) {
        this.embeddableAssembler = embeddableAssembler;
    }

    @Mapping(target = "id", expression = "java(embeddableAssembler.map(customer.id()))")
    @Mapping(target = "firstName", expression = "java(embeddableAssembler.toFirstName(customer.fullName()))")
    @Mapping(target = "lastName", expression = "java(embeddableAssembler.toLastName(customer.fullName()))")
    @Mapping(target = "birthDate", expression = "java(embeddableAssembler.map(customer.birthDate()))")
    @Mapping(target = "email", expression = "java(embeddableAssembler.map(customer.email()))")
    @Mapping(target = "phone", expression = "java(embeddableAssembler.map(customer.phone()))")
    @Mapping(target = "document", expression = "java(embeddableAssembler.map(customer.document()))")
    @Mapping(target = "registeredAt", expression = "java(customer.registeredAt())")
    @Mapping(target = "archivedAt", expression = "java(customer.archivedAt())")
    @Mapping(target = "loyaltyPoints", expression = "java(embeddableAssembler.map(customer.loyaltyPoints()))")
    @Mapping(target = "address", expression = "java(embeddableAssembler.map(customer.address()))")
    @Mapping(target = "version", expression = "java(customer.version())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    public abstract CustomerPersistenceEntity toDomain(@MappingTarget final CustomerPersistenceEntity entity,
                                       final Customer customer);


}
