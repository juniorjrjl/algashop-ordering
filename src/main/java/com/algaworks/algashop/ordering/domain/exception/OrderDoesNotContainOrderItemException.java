package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderItemId;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.ERROR_DOES_NOT_CONTAIN_ITEM;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(final OrderId id,
                                                 final OrderItemId orderItemId) {
        final var message = String.format(ERROR_DOES_NOT_CONTAIN_ITEM, id, orderItemId);
        super(message);
    }
}
