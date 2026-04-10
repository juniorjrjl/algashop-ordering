package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.DomainEntityNotFoundException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_SHOPPING_CART_NOT_FOUND;

public class ShoppingCartNotFoundException extends DomainEntityNotFoundException {

    public ShoppingCartNotFoundException() {
    }

    public ShoppingCartNotFoundException(final ShoppingCartId shoppingCartId) {
        super(String.format(ERROR_SHOPPING_CART_NOT_FOUND, shoppingCartId));
    }
}
