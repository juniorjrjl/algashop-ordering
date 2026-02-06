package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO;
import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS;
import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD;
import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO;

public class OrderCannotBePlacedException extends DomainException {

    private OrderCannotBePlacedException(final String message) {
        super(message);
    }

    public static OrderCannotBePlacedException noItems(final OrderId orderId) {
        final var message = String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_ITEMS, orderId);
        return new OrderCannotBePlacedException(message);
    }

    public static OrderCannotBePlacedException noShippingInfo(final OrderId orderId) {
        final var message = String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_SHIPPING_INFO, orderId);
        return new OrderCannotBePlacedException(message);
    }

    public static OrderCannotBePlacedException noBillingInfo(final OrderId orderId) {
        final var message = String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_BILLING_INFO, orderId);
        return new OrderCannotBePlacedException(message);
    }

    public static OrderCannotBePlacedException noPaymentMethod(final OrderId orderId) {
        final var message = String.format(ERROR_ORDER_CANNOT_BE_PLACED_HAS_NO_PAYMENT_METHOD, orderId);
        return new OrderCannotBePlacedException(message);
    }

}
