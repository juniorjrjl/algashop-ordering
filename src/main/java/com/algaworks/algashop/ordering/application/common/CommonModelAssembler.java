package com.algaworks.algashop.ordering.application.common;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CommonModelAssembler {

    Address toAddress(final AddressData addressData);

    ZipCode mapZipCode(final String value);

}
