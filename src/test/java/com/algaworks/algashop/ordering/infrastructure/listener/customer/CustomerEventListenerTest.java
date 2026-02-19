package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.application.customer.notification.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CustomerEventListenerTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final ApplicationEventPublisher applicationEventPublisher;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoBean
    private CustomerLoyaltyPointsApplicationService loyaltyPointsApplicationService;

    @MockitoSpyBean
    private CustomerNotificationApplicationService notificationApplicationService;

    @Autowired
    CustomerEventListenerTest(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
    }

    @Test
    void shouldListenOrderReadyEvent() {
        applicationEventPublisher.publishEvent(
                new OrderReadyEvent(
                        new OrderId(),
                        new CustomerId(),
                        OffsetDateTime.now()
                )
        );

        verify(customerEventListener).listen(any(OrderReadyEvent.class));

        verify(loyaltyPointsApplicationService).addLoyaltyPoints(
                any(UUID.class),
                any(String.class)
        );
    }

    @Test
    void shouldListenCustomerRegisteredEvent() {
        applicationEventPublisher.publishEvent(
                new CustomerRegisteredEvent(
                        new CustomerId(),
                        customFaker.common().fullName(),
                        customFaker.common().email(),
                        OffsetDateTime.now()
                )
        );

        verify(customerEventListener).listen(any(CustomerRegisteredEvent.class));

        verify(notificationApplicationService)
                .notifyNewRegistration(any(NotifyNewRegistrationInput.class));
    }

}