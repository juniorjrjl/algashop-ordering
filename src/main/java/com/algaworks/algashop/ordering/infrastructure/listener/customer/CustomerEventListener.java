package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.application.customer.notification.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventListener {

    private final CustomerNotificationApplicationService notificationApplicationService;
    private final CustomerLoyaltyPointsApplicationService loyaltyPointsApplicationService;

    @EventListener(CustomerRegisteredEvent.class)
    public void listen(final CustomerRegisteredEvent event){
        log.info("CustomerRegisteredEvent received: {}", event);
        final var input = new NotifyNewRegistrationInput(
                event.customerId().value(),
                event.fullName().firstName(),
                event.email().value()
        );
        notificationApplicationService.notifyNewRegistration(input);
    }

    @EventListener(CustomerArchivedEvent.class)
    public void listen(final CustomerArchivedEvent event){
        log.info("CustomerArchivedEvent received: {}", event);
    }

    @EventListener(OrderReadyEvent.class)
    public void listen(final OrderReadyEvent event){
        loyaltyPointsApplicationService.addLoyaltyPoints(
                event.customerId().value(),
                event.orderId().toString()
        );
    }

}
