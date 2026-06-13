package com.algaworks.algashop.ordering.core.domain.model.customer;

import com.algaworks.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.ErrorMessage;

public class CustomerNotFoundException extends DomainEntityNotFoundException {

    public CustomerNotFoundException() {

    }

    public CustomerNotFoundException(final CustomerId customerId) {
        super(String.format(ErrorMessage.ERROR_CUSTOMER_NOT_FOUND, customerId));
    }
}
