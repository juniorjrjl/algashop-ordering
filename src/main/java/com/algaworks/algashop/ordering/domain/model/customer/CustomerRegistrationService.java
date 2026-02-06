package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
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
