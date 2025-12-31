package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ShoppingCartItemId;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_ITEM;
import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT;

public class ShoppingCartDoesNotContainProductException extends DomainException {

    public ShoppingCartDoesNotContainProductException(final ShoppingCartId id,
                                                      final ProductId productId) {
        final var message = String.format(ERROR_SHOPPING_CART_DOES_NOT_CONTAIN_PRODUCT, id, productId);
        super(message);
    }
}
