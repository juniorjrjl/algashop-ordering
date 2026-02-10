package com.algaworks.algashop.ordering.application.common;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@AnnotateWith(NullMarked.class)
@Mapper(componentModel = SPRING)
public interface CommonDisassembler {

    Address toAddress(final AddressData addressData);

    ZipCode mapZipCode(final String value);

    FullName toFullName(final String firstName, final String lastName);

    Document toDocument(final String value);

    Phone toPhone(final String value);

    Email toEmail(final String value);

    @Mapping(target = "add", ignore = true)
    @Mapping(target = "multiply", ignore = true)
    @Mapping(target = "divide", ignore = true)
    Money toMoney(final String value);

}
