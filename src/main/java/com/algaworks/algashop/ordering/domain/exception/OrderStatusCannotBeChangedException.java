package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

public class OrderStatusCannotBeChangedException extends DomainException {


    public OrderStatusCannotBeChangedException(final OrderId id,
                                               final OrderStatus orderStatus,
                                               final OrderStatus newStatus) {
        final var errorMessage = String.format(
                ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,
                id,
                orderStatus,
                newStatus
        );
        super(errorMessage);
    }
}
