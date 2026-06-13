package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.port.in.customer.ForAddingLoyaltyPoints;
import com.algaworks.algashop.ordering.core.port.out.customer.ForNotifyingCustomer;
import com.algaworks.algashop.ordering.core.port.out.customer.NotifyNewRegistrationInput;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import com.algaworks.algashop.ordering.infrastructure.adapter.in.listener.customer.CustomerEventListener;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
class CustomerEventListenerTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final ApplicationEventPublisher applicationEventPublisher;
    private final Customers customers;

    @MockitoSpyBean
    private CustomerEventListener customerEventListener;

    @MockitoBean
    private ForAddingLoyaltyPoints loyaltyPointsApplicationService;

    @MockitoSpyBean
    private ForNotifyingCustomer notificationApplicationService;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    CustomerEventListenerTest(final ApplicationEventPublisher applicationEventPublisher,
                              final Customers customers) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.customers = customers;
    }

    @DynamicPropertySource
    public static void configurePropertySource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
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
        final var customer = CustomerDataBuilder.builder().buildExisting();
        customers.add(customer);
        applicationEventPublisher.publishEvent(
                new CustomerRegisteredEvent(
                        customer.id(),
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