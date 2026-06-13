package com.algaworks.algashop.ordering.infrastructure.adapter.out.notification.customer;

import com.algaworks.algashop.ordering.core.port.out.customer.ForNotifyingCustomer;
import com.algaworks.algashop.ordering.core.port.out.customer.NotifyNewRegistrationInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForNotifyingCustomerFakeImpl implements ForNotifyingCustomer {

    @Override
    public void notifyNewRegistration(final NotifyNewRegistrationInput input) {
        log.info("New registration of customer  {}", input);
    }
}
