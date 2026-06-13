package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.DomainException;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessage.ORDER_NOT_ALLOW_CHANGES_EXCEPTION;

public class OrderCannotBeEditedException extends DomainException {

    public OrderCannotBeEditedException(final OrderId id, final OrderStatus orderStatus) {
        final var message = String.format(ORDER_NOT_ALLOW_CHANGES_EXCEPTION, id, orderStatus);
        super(message);
    }

}
