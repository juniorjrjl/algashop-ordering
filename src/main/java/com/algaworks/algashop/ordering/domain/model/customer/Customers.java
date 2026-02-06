package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.Repository;
import com.algaworks.algashop.ordering.domain.model.commons.Email;

import java.util.Optional;

public interface Customers extends Repository<Customer, CustomerId> {

    Optional<Customer> ofEmail(final Email email);

    boolean isEmailUnique(final Email email, final CustomerId exceptCustomerId);

}
