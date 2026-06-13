package com.algaworks.algashop.ordering.core.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.DomainException;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessage.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM;

public class ShoppingCartDoesNotContainOrderItemException extends DomainException {

    public ShoppingCartDoesNotContainOrderItemException(final ShoppingCartId id,
                                                        final ShoppingCartItemId itemId) {
        final var message = String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM, id, itemId);
        super(message);
    }
}
