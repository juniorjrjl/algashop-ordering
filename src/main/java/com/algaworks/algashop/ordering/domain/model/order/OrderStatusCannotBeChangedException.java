package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

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
