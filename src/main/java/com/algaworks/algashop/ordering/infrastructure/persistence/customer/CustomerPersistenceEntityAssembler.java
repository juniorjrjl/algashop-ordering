package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssembler;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = EmbeddableAssembler.class)
public abstract class CustomerPersistenceEntityAssembler {

    @Nullable
    protected EmbeddableAssembler embeddableAssembler;

    @Autowired
    public void setEmbeddableAssembler(final EmbeddableAssembler embeddableAssembler) {
        this.embeddableAssembler = embeddableAssembler;
    }

    public EmbeddableAssembler getEmbeddableAssembler() {
        return requireNonNull(embeddableAssembler, "embeddableAssembler must be injected by Spring");
    }

    @Mapping(target = "id", expression = "java(getEmbeddableAssembler().map(customer.id()))")
    @Mapping(target = "firstName", expression = "java(getEmbeddableAssembler().toFirstName(customer.fullName()))")
    @Mapping(target = "lastName", expression = "java(getEmbeddableAssembler().toLastName(customer.fullName()))")
    @Mapping(target = "birthDate", expression = "java(map(customer.birthDate()))")
    @Mapping(target = "email", expression = "java(getEmbeddableAssembler().map(customer.email()))")
    @Mapping(target = "phone", expression = "java(getEmbeddableAssembler().map(customer.phone()))")
    @Mapping(target = "document", expression = "java(getEmbeddableAssembler().map(customer.document()))")
    @Mapping(target = "registeredAt", expression = "java(customer.registeredAt())")
    @Mapping(target = "archivedAt", expression = "java(customer.archivedAt())")
    @Mapping(target = "loyaltyPoints", expression = "java(map(customer.loyaltyPoints()))")
    @Mapping(target = "address", expression = "java(getEmbeddableAssembler().map(customer.address()))")
    @Mapping(target = "version", expression = "java(customer.version())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    public abstract CustomerPersistenceEntity toDomain(@MappingTarget final CustomerPersistenceEntity entity,
                                       final Customer customer);

    protected LocalDate map(final BirthDate birthDate) {
        return isNull(birthDate) ? null : birthDate.value();
    }

    protected Integer map(final LoyaltyPoints loyaltyPoints) {
        return isNull(loyaltyPoints) ? null : loyaltyPoints.value();
    }

}
