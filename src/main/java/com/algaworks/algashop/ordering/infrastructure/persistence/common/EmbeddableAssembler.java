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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
@AnnotateWith(NullMarked.class)
public interface EmbeddableAssembler {

    AddressEmbeddable map(final Address address);

    default String map(final Document document){
        return  document.value();
    }

    default String map(final Email email) {
        return email.value();
    }

    default String toFirstName(final FullName fullName) {
        return fullName.firstName();
    }

    default String toLastName(final FullName fullName) {
        return fullName.lastName();
    }

    @Nullable
    default BigDecimal map(@Nullable final Money money) {
        return isNull(money) ? null : money.value();
    }

    default String map(final Phone phone){
        return phone.value();
    }

    @Nullable
    default Integer map(@Nullable final Quantity quantity) {
        return isNull(quantity) ? null : quantity.value();
    }

    default String map(final ZipCode zipCode){
        return zipCode.value();
    }

    default String map(final ProductName productName) {
        return productName.value();
    }

    default UUID map(final CustomerId customerId) {
        return customerId.value();
    }

    default UUID map(final ProductId productId){
        return productId.value();
    }

}
