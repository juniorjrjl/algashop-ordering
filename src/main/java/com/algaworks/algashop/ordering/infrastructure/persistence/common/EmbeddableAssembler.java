package com.algaworks.algashop.ordering.infrastructure.persistence.common;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EmbeddableAssembler {

    AddressEmbeddable map(final Address address);

    default String map(final Document document){
        return  isNull(document) ? null : document.value();
    }

    default String map(final Email email) {
        return isNull(email) ? null : email.value();
    }

    default String toFirstName(final FullName fullName) {
        return isNull(fullName) ? null : fullName.firstName();
    }

    default String toLastName(final FullName fullName) {
        return isNull(fullName) ? null : fullName.lastName();
    }

    default BigDecimal map(final Money money) {
        return isNull(money) ? null : money.value();
    }

    default String map(final Phone phone){
        return  isNull(phone) ? null : phone.value();
    }

    default Integer map(final Quantity quantity) {
        return isNull(quantity) ? null : quantity.value();
    }

    default String map(final ZipCode zipCode){
        return isNull(zipCode) ? null : zipCode.value();
    }

    default String map(final ProductName productName) {
        return isNull(productName) ? null : productName.value();
    }

    default UUID map(final CustomerId customerId) {
        return isNull(customerId) ? null : customerId.value();
    }

    default UUID map(final ProductId productId){
        return  isNull(productId) ? null : productId.value();
    }

}
