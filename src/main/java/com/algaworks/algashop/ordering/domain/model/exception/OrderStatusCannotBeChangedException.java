package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessage.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

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
