package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.common.CommonAssembler;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public abstract class CustomerDisassembler {

    protected CommonAssembler commonModelDisassembler;

    @Autowired
    public void setCommonModelDisassembler(final CommonAssembler commonModelDisassembler) {
        this.commonModelDisassembler = commonModelDisassembler;
    }

    @Mapping(target = "id", expression = "java(map(customer.id()))")
    @Mapping(target = "firstName", expression = "java(mapFirstName(customer.fullName()))")
    @Mapping(target = "lastName", expression = "java(mapLastName(customer.fullName()))")
    @Mapping(target = "birthDate", expression = "java(map(customer.birthDate()))")
    @Mapping(target = "email", expression = "java(map(customer.email()))")
    @Mapping(target = "phone", expression = "java(map(customer.phone()))")
    @Mapping(target = "document", expression = "java(map(customer.document()))")
    @Mapping(target = "registeredAt", expression = "java(customer.registeredAt())")
    @Mapping(target = "archivedAt", expression = "java(customer.archivedAt())")
    @Mapping(target = "loyaltyPoints", expression = "java(map(customer.loyaltyPoints()))")
    @Mapping(target = "address", expression = "java(commonModelDisassembler.toAddressData(customer.address()))")
    abstract CustomerOutput toOutput(final Customer customer);

    protected UUID map(final CustomerId customerId) {
        return requireNonNull(customerId).value();
    }

    protected String mapFirstName(final FullName fullName) {
        return requireNonNull(fullName).firstName();
    }

    protected String mapLastName(final FullName fullName) {
        return requireNonNull(fullName).lastName();
    }

    protected LocalDate map(final BirthDate birthDate){
        return requireNonNull(birthDate).value();
    }

    protected String map(final Email email){
        return requireNonNull(email).value();
    }

    protected String map(final Phone phone){
        return requireNonNull(phone).value();
    }

    protected String map(final Document document){
        return requireNonNull(document).value();
    }

    protected Integer map(final LoyaltyPoints loyaltyPoints){
        return requireNonNull(loyaltyPoints).value();
    }

}
