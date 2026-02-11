package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerEmailInUseException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerInputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerUpdateInputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.Assertions.in;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CustomerManagementApplicationServiceTest extends AbstractApplicationTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @MockitoSpyBean
    private CustomerEventListener listener;

    @MockitoSpyBean
    private CustomerNotificationApplicationService notificationApplicationService;

    private final CustomerManagementApplicationService service;
    private final Customers customers;

    @Autowired
    CustomerManagementApplicationServiceTest(final JdbcTemplate jdbcTemplate,
                                             final CustomerManagementApplicationService service,
                                             final Customers customers) {
        super(jdbcTemplate);
        this.service = service;
        this.customers = customers;
    }

    @Test
    void shouldRegister(){
        final var input = CustomerInputDataBuilder.builder().build();
        final var actual = service.create(input);
        assertThat(actual).isNotNull();
        verify(listener).listen(any(CustomerRegisteredEvent.class));
        final var eventInput = new CustomerNotificationApplicationService.
                NotifyNewRegistrationInput(actual, input.getFirstName(), input.getEmail());
        verify(notificationApplicationService).notifyNewRegistration(eventInput);
    }

    @Test
    void shouldFindById(){
        final var input = CustomerInputDataBuilder.builder().build();
        final var id = service.create(input);
        final var actual = service.findById(id);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id", "registeredAt", "archived", "archivedAt", "loyaltyPoints")
                .isEqualTo(input);
    }

    @Test
    void givenNonStoredIdWhenFindByIdThenThrowException(){
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.findById(UUID.randomUUID()));
    }

    @Test
    void shouldUpdate(){
        final var inserted = CustomerInputDataBuilder.builder().build();
        final var id = service.create(inserted);
        final var input = CustomerUpdateInputDataBuilder.builder().build();
        service.update(id, input);
        final var actual = service.findById(id);
        assertThat(actual).extracting(
                CustomerOutput::getFirstName,
                CustomerOutput::getLastName,
                CustomerOutput::getPhone,
                CustomerOutput::isPromotionNotificationsAllowed,
                CustomerOutput::getAddress
        ).containsExactly(
                input.getFirstName(),
                input.getLastName(),
                input.getPhone(),
                input.isPromotionNotificationsAllowed(),
                input.getAddress()
        );
    }

    @Test
    void givenNonStoredIdWhenUpdateThenThrowException(){
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.update(UUID.randomUUID(), CustomerUpdateInput.builder().build()));
    }

    @Test
    void givenStoredCustomerWhenArchivedThenSaveIt(){
        final var customer = CustomerDataBuilder.builder()
                .buildNew();
        customers.add(customer);
        service.archive(customer.id().value());
        final var actual = service.findById(customer.id().value());
        assertWith(actual,
                a -> assertThat(a.getArchived()).isTrue(),
                a -> assertThat(a.getArchivedAt()).isNotNull(),
                a -> assertThat(a.getFirstName()).isEqualTo(FullName.ANONYMOUS.firstName()),
                a -> assertThat(a.getLastName()).isEqualTo(FullName.ANONYMOUS.lastName()),
                a -> assertThat(a.getPhone()).isEqualTo(Phone.ANONYMOUS.value()),
                a -> assertThat(a.getDocument()).isEqualTo(Document.ANONYMOUS.value()),
                a -> assertThat(a.getEmail()).isEqualTo(Email.ANONYMOUS.value()),
                a -> assertThat(a.getBirthDate()).isNull()
        );
        assertWith(actual.getAddress(),
                a -> assertThat(a.getNumber()).isEqualTo("Anonymous"),
                a -> assertThat(a.getComplement()).isNull()
        );
        verify(listener).listen(any(CustomerArchivedEvent.class));
    }

    @Test
    void givenNonStoredCustomerWhenArchiveThenThrowException(){
        assertThatExceptionOfType(CustomerNotFoundException.class)
            .isThrownBy(() -> service.archive(UUID.randomUUID()));
    }

    @Test
    void givenValidEmailWhenChangeEmailThenSaveIt(){
        final var customer = CustomerDataBuilder.builder()
                .buildNew();
        customers.add(customer);
        final var newEmail = customFaker.internet().safeEmailAddress();
        service.changeEmail(customer.id().value(), newEmail);
        final var actual = service.findById(customer.id().value());
        assertThat(actual.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void givenNonStoredCustomerIdAndValidEmailWhenChangeEmailThenThrowException(){
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.changeEmail(
                        UUID.randomUUID(),
                        customFaker.internet().safeEmailAddress())
                );
    }

    @Test
    void givenCustomerAndValidEmailWhenChangeEmailThenThrowException(){
        final var customer = CustomerDataBuilder.builder()
                .buildNew();
        customer.archive();
        customers.add(customer);
        final var newEmail = customFaker.internet().safeEmailAddress();
        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> service.changeEmail(
                        customer.id().value(),
                        newEmail)
                );
    }

    @Test
    void givenCustomerAndInvalidEmailWhenChangeEmailThenThrowException(){
        final var customer = CustomerDataBuilder.builder()
                .buildNew();
        customer.archive();
        customers.add(customer);
        final var newEmail = customFaker.lorem().word();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> service.changeEmail(
                        customer.id().value(),
                        newEmail)
                );
    }

    @Test
    void givenEmailInUseWhenChangeEmailThenThrowException(){
        final var customer = CustomerDataBuilder.builder()
                .buildNew();
        final var stored = CustomerDataBuilder.builder()
                .buildNew();
        customers.add(customer);
        customers.add(stored);
        assertThatExceptionOfType(CustomerEmailInUseException.class)
                .isThrownBy(() -> service.changeEmail(
                        customer.id().value(),
                        stored.email().value())
                );
    }

}