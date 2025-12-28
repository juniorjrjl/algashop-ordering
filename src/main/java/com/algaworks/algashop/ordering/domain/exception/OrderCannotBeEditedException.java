package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.ORDER_NOT_ALLOW_CHANGES_EXCEPTION;

public class OrderCannotBeEditedException extends DomainException {

    public OrderCannotBeEditedException(final OrderId id, final OrderStatus orderStatus) {
        final var message = String.format(ORDER_NOT_ALLOW_CHANGES_EXCEPTION, id, orderStatus);
        super(message);
    }

}
