package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.port.in.common.CommonDisassembler;
import com.algaworks.algashop.ordering.core.domain.model.commons.Document;
import com.algaworks.algashop.ordering.core.domain.model.commons.Email;
import com.algaworks.algashop.ordering.core.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.core.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.core.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerRegistrationService;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerInput;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerUpdateInput;
import com.algaworks.algashop.ordering.core.port.in.customer.ForManagingCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService implements ForManagingCustomer {

    private final CustomerRegistrationService service;
    private final Customers customers;
    private final CommonDisassembler commonModelAssembler;

    @Transactional
    @Override
    public UUID create(final CustomerInput input){
        final var customer = service.register(
                new FullName(input.getFirstName(), input.getLastName()),
                new BirthDate(input.getBirthDate()),
                new Email(input.getEmail()),
                new Phone(input.getPhone()),
                new Document(input.getDocument()),
                input.getPromotionNotificationsAllowed(),
                commonModelAssembler.toAddress(input.getAddress())
        );
        customers.add(customer);
        return customer.id().value();
    }

    @Transactional
    @Override
    public void update(final UUID rawId, final CustomerUpdateInput input){
        final var customer = customers.ofId(new CustomerId(rawId))
                .orElseThrow(CustomerNotFoundException::new);
        customer.changeFullName(new  FullName(input.getFirstName(), input.getLastName()));
        customer.changePhone(new  Phone(input.getPhone()));
        customer.changeAddress(commonModelAssembler.toAddress(input.getAddress()));
        if (input.getPromotionNotificationsAllowed()){
            customer.enablePromotionNotifications();
        } else {
            customer.disablePromotionNotifications();
        }
        customers.add(customer);
    }

    @Transactional
    @Override
    public void archive(final UUID rawId){
        final var customer = customers.ofId(new CustomerId(rawId))
                .orElseThrow(CustomerNotFoundException::new);
        customer.archive();
        customers.add(customer);
    }

    @Override
    public void changeEmail(final UUID rawId, final String email){
        final var customer = customers.ofId(new CustomerId(rawId))
                .orElseThrow(CustomerNotFoundException::new);
        service.changeEmail(customer, new Email(email));
        customers.add(customer);
    }

}
