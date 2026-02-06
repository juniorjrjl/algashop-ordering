package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM;

public class OrderDoesNotContainOrderItemException extends DomainException {

    public OrderDoesNotContainOrderItemException(final OrderId id,
                                                 final OrderItemId orderItemId) {
        final var message = String.format(ERROR_ORDER_DOES_NOT_CONTAIN_ITEM, id, orderItemId);
        super(message);
    }
}
