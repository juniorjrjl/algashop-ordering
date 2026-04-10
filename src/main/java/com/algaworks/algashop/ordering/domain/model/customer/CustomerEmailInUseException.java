package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessage.ERROR_CUSTOMER_EMAIL_IS_IN_USE;

public class CustomerEmailInUseException extends DomainException {

    public CustomerEmailInUseException() {
        super(ERROR_CUSTOMER_EMAIL_IS_IN_USE);
    }
}
