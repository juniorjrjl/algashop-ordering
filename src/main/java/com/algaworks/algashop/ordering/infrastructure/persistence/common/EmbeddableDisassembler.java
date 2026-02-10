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
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@AnnotateWith(NullMarked.class)
@Mapper(componentModel = SPRING)
public interface EmbeddableDisassembler {

    Address toAddress(final AddressEmbeddable address);

    Document toDocument(final String value);

    Email toEmail(final String value);

    FullName toFullName(final String firstName, final String lastName);

    @Mapping(target = "add", ignore = true)
    @Mapping(target = "multiply", ignore = true)
    @Mapping(target = "divide", ignore = true)
    Money toMoney(final BigDecimal value);

    Phone toPhone(final String value);

    @Mapping(target = "add", ignore = true)
    Quantity toQuantity(final Integer value);

    ZipCode toZipCode(final String value);

    CustomerId toCustomerId(final UUID value);

    default ProductId toProductId(final UUID value){
        return new ProductId(value);
    }

    ProductName toProductName(final String value);

}
