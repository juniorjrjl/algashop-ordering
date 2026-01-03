package com.algaworks.algashop.ordering.domain.model.exception;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessage.ERROR_CUSTOMER_ARCHIVED;

public class CustomerArchivedException extends DomainException{

    public CustomerArchivedException(final Throwable cause) {
        super(ERROR_CUSTOMER_ARCHIVED, cause);
    }

    public CustomerArchivedException() {
        super(ERROR_CUSTOMER_ARCHIVED);
    }

}
