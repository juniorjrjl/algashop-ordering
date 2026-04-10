package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_CUSTOMER_ALREADY_HAVE_SHOPPING_CART;

public class CustomerAlreadyHaveShoppingCartException extends DomainException {

    public CustomerAlreadyHaveShoppingCartException(final CustomerId customerId) {
        super(String.format(ERROR_CUSTOMER_ALREADY_HAVE_SHOPPING_CART, customerId));
    }
}
