package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EmbeddableAssembler {

    AddressEmbeddable map(final Address address);

    ShippingEmbeddable map(final Shipping shipping);

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    RecipientEmbeddable map(final Recipient recipient);

    @Mapping(target = "firstName", source = "fullName.firstName")
    @Mapping(target = "lastName", source = "fullName.lastName")
    BillingEmbeddable map(final Billing billing);

    String map(final OrderStatus status);

    String map(final PaymentMethod method);

    default String map(final ProductName productName) {
        return isNull(productName) ? null : productName.value();
    }

    default Integer map(final Quantity quantity) {
        return isNull(quantity) ? null : quantity.value();
    }

    default String map(final ZipCode zipCode){
        return isNull(zipCode) ? null : zipCode.value();
    }

    default LocalDate map(final BirthDate birthDate) {
        return isNull(birthDate) ? null : birthDate.value();
    }

    default Integer map(final LoyaltyPoints loyaltyPoints) {
        return isNull(loyaltyPoints) ? null : loyaltyPoints.value();
    }

    default Long map(final OrderItemId orderItemId) {
        return orderItemId.value().toLong();
    }

    default Long map(final OrderId orderId) {
        return orderId.value().toLong();
    }

    default UUID map(final CustomerId customerId) {
        return isNull(customerId) ? null : customerId.value();
    }

    default UUID map(final ProductId productId){
        return  isNull(productId) ? null : productId.value();
    }

    default String toFirstName(final FullName fullName) {
        return isNull(fullName) ? null : fullName.firstName();
    }

    default String toLastName(final FullName fullName) {
        return isNull(fullName) ? null : fullName.lastName();
    }

    default String map(final Email email) {
        return isNull(email) ? null : email.value();
    }

    default String map(final Phone phone){
        return  isNull(phone) ? null : phone.value();
    }

    default String map(final Document document){
        return  isNull(document) ? null : document.value();
    }

    default BigDecimal map(final Money money) {
        return isNull(money) ? null : money.value();
    }

}
