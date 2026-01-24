package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerEmailInUseException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private final Customers customers;

    public Customer register(final FullName fullName,
                             final BirthDate birthDate,
                             final Email email,
                             final Phone phone,
                             final Document document,
                             final Boolean promotionNotificationsAllowed,
                             final Address address){
        final var customer = Customer.brandNew()
                .fullName(fullName)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .document(document)
                .promotionNotificationsAllowed(promotionNotificationsAllowed)
                .address(address)
                .build();
        verifyEmailUniqueness(customer.email(), customer.id());

        return customer;
    }

    public void changeEmail(final Customer customer, final Email newEmail){
        verifyEmailUniqueness(newEmail, customer.id());
        customer.changeEmail(newEmail);
    }

    private void verifyEmailUniqueness(final Email email, final CustomerId id) {
        if(!customers.isEmailUnique(email, id)){
            throw new CustomerEmailInUseException();
        }
    }

}
