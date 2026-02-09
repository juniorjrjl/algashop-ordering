package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.common.CommonDisassembler;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

    private final CustomerRegistrationService service;
    private final Customers customers;
    private final CommonDisassembler commonModelAssembler;
    private final CustomerDisassembler disassembler;

    @Transactional
    public UUID create(@NonNull final CustomerInput input){
        final var customer = service.register(
                new FullName(input.getFirstName(), input.getLastName()),
                new BirthDate(input.getBirthDate()),
                new Email(input.getEmail()),
                new Phone(input.getPhone()),
                new Document(input.getDocument()),
                input.isPromotionNotificationsAllowed(),
                commonModelAssembler.toAddress(input.getAddress())
        );
        customers.add(customer);
        return customer.id().value();
    }

    @Transactional
    public void update(@NonNull final UUID rawId, @NonNull final CustomerUpdateInput input){
        final var customer = customers.ofId(new CustomerId(rawId))
                .orElseThrow(CustomerNotFoundException::new);
        customer.changeFullName(new  FullName(input.getFirstName(), input.getLastName()));
        customer.changePhone(new  Phone(input.getPhone()));
        customer.changeAddress(commonModelAssembler.toAddress(input.getAddress()));
        if (input.isPromotionNotificationsAllowed()){
            customer.enablePromotionNotifications();
        } else {
            customer.disablePromotionNotifications();
        }
        customers.add(customer);
    }

    @Transactional
    public void archive(@NonNull final UUID rawId){
        final var customer = customers.ofId(new CustomerId(rawId))
                .orElseThrow(CustomerNotFoundException::new);
        customer.archive();
        customers.add(customer);
    }

    public void changeEmail(@NonNull final UUID rawId, @NonNull final String email){
        final var customer = customers.ofId(new CustomerId(rawId))
                .orElseThrow(CustomerNotFoundException::new);
        service.changeEmail(customer, new Email(email));
        customers.add(customer);
    }

    @Transactional(readOnly = true)
    public CustomerOutput findById(@NonNull final UUID rawId){
        final var customer = customers.ofId(new CustomerId(rawId))
                .orElseThrow(CustomerNotFoundException::new);
        return disassembler.toOutput(customer);
    }

}
