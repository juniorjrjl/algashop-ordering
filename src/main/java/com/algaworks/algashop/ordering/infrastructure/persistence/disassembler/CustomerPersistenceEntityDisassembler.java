package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = EmbeddableDisassembler.class, injectionStrategy = CONSTRUCTOR)
public interface CustomerPersistenceEntityDisassembler {

    @Mapping(target = "fullName",
            expression = "java(embeddableDisassembler.toFullName(entity.getFirstName(), entity.getLastName()))"
    )
    Customer.ExistingCustomerBuilder toDomain(@MappingTarget final Customer.ExistingCustomerBuilder builder,
                                              final CustomerPersistenceEntity entity);

    default Customer toDomain(final CustomerPersistenceEntity entity) {
        return toDomain(Customer.existing(), entity).build();
    }

}
