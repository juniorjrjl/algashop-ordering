package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.algaworks.algashop.ordering.domain.model.ErrorMessage;

public class ProductNotFoundException extends DomainEntityNotFoundException {

    public ProductNotFoundException() {
    }

    public ProductNotFoundException(final ProductId productId) {
        super(String.format(ErrorMessage.ERROR_PRODUCT_NOT_FOUND, productId));
    }
}
