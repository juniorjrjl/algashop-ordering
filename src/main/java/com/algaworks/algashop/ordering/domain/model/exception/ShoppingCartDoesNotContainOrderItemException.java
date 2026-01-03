package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessage.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM;

public class ShoppingCartDoesNotContainOrderItemException extends DomainException {

    public ShoppingCartDoesNotContainOrderItemException(final ShoppingCartId id,
                                                        final ShoppingCartItemId itemId) {
        final var message = String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM, id, itemId);
        super(message);
    }
}
