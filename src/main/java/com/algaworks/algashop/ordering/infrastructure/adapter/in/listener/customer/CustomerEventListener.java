package com.algaworks.algashop.ordering.infrastructure.adapter.in.listener.customer;

import com.algaworks.algashop.ordering.core.port.in.customer.ForAddingLoyaltyPoints;
import com.algaworks.algashop.ordering.core.port.in.customer.ForConfirmCustomerRegistration;
import com.algaworks.algashop.ordering.core.port.out.customer.ForNotifyingCustomer;
import com.algaworks.algashop.ordering.core.port.out.customer.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerArchivedEvent;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

    private final ForConfirmCustomerRegistration forConfirmCustomerRegistration;
    private final ForAddingLoyaltyPoints forAddingLoyaltyPoints;

    @EventListener(CustomerRegisteredEvent.class)
    public void listen(final CustomerRegisteredEvent event){
        log.info("CustomerRegisteredEvent received: {}", event);
        forConfirmCustomerRegistration.confirm(event.customerId().value());
    }

    @EventListener(CustomerArchivedEvent.class)
    public void listen(final CustomerArchivedEvent event){
        log.info("CustomerArchivedEvent received: {}", event);
    }

    @EventListener(OrderReadyEvent.class)
    public void listen(final OrderReadyEvent event){
        forAddingLoyaltyPoints.addLoyaltyPoints(
                event.customerId().value(),
                event.orderId().toString()
        );
    }

}
