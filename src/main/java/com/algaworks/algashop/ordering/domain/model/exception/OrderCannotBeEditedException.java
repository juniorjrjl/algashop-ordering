package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessage.ORDER_NOT_ALLOW_CHANGES_EXCEPTION;

public class OrderCannotBeEditedException extends DomainException {

    public OrderCannotBeEditedException(final OrderId id, final OrderStatus orderStatus) {
        final var message = String.format(ORDER_NOT_ALLOW_CHANGES_EXCEPTION, id, orderStatus);
        super(message);
    }

}
