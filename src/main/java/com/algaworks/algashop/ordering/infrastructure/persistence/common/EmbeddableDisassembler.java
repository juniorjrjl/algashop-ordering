package com.algaworks.algashop.ordering.infrastructure.persistence.common;

import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.ShippingEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface EmbeddableDisassembler {

    LoyaltyPoints toLoyaltyPoints(final Integer value);

    BirthDate toBirthDate(final LocalDate value);

    ProductName toProductName(final String value);

    Quantity toQuantity(final Integer value);

    Money toMoney(final BigDecimal value);

    ZipCode toZipCode(final String value);

    CustomerId toCustomerId(final UUID value);

    ShoppingCartId toShoppingCartId(final UUID value);

    ShoppingCartItemId toShoppingCartItemId(final UUID value);

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
