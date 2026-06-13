package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.port.in.customer.ForConfirmCustomerRegistration;
import com.algaworks.algashop.ordering.core.port.out.customer.ForNotifyingCustomer;
import com.algaworks.algashop.ordering.core.port.out.customer.ForObtainingCustomer;
import com.algaworks.algashop.ordering.core.port.out.customer.NotifyNewRegistrationInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationConfirmationApplicationService implements ForConfirmCustomerRegistration {

    private final ForNotifyingCustomer forNotifyingCustomer;
    private final ForObtainingCustomer forObtainingCustomer;

    public void confirm(final UUID customerId) {
        final var output = forObtainingCustomer.findById(customerId);
        final var input = new NotifyNewRegistrationInput(
                customerId,
                output.getFirstName(),
                output.getEmail()
        );
        forNotifyingCustomer.notifyNewRegistration(input);
    }

}
