package com.algaworks.algashop.ordering.domain.exception;

import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.ERROR_PRODUCT_IS_OUT_OF_STOCK;

public class OutOfStockException extends DomainException {

    public OutOfStockException(final ProductId id) {
        final var message = String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK, id);
        super(message);
    }

}
