package com.algaworks.algashop.ordering.application.common;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import org.mapstruct.Mapper;

import static java.util.Objects.requireNonNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CommonModelDisassembler {

    AddressData toAddressData(Address address);

    default String map(final ZipCode zipCode){
        return requireNonNull(zipCode).value();
    }

}
