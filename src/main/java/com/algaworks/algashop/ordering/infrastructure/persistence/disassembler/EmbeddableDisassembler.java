package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
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
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EmbeddableDisassembler {

    ProductName toProductName(final String value);

    Quantity toQuantity(final Integer value);

    Money toMoney(final BigDecimal value);

    ZipCode toZipCode(final String value);

    CustomerId toCustomerId(final UUID value);

    Address toAddress(final AddressEmbeddable address);

    @Mapping(target = "fullName", expression = "java(toFullName(recipient.getFirstName(), recipient.getLastName()))")
    Recipient toRecipient(final RecipientEmbeddable recipient);

    Shipping toShipping(final ShippingEmbeddable shipping);

    @Mapping(target = "fullName", expression = "java(toFullName(billing.getFirstName(), billing.getLastName()))")
    Billing toBilling(final BillingEmbeddable billing);

    FullName toFullName(final String firstName, final String lastName);

    Email toEmail(final String value);

    Phone toPhone(final String value);

    Document toDocument(final String value);

    Phone toPhone(final Phone phone);

    default OrderStatus mapOrderStatus(final String status) {
        return isNull(status) ? null : OrderStatus.valueOf(status);
    }

    default PaymentMethod mapPaymentMethod(final String method) {
        return isNull(method) ? null : PaymentMethod.valueOf(method);
    }

    default OrderId toOrderId(final Long value){
        return isNull(value) ? null : new OrderId(value);
    }

    default OrderItemId toOrderItemId(final Long value){
        return isNull(value) ? null : new OrderItemId(value);
    }

    default ProductId toProductId(final UUID value){
        return isNull(value) ? null : new ProductId(value);
    }

}
