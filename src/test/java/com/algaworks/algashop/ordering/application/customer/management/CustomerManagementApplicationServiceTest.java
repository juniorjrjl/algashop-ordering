package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerInputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerUpdateInputDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@SpringBootTest
@IntegrationTest
class CustomerManagementApplicationServiceTest {

    private final CustomerManagementApplicationService service;

    @Autowired
    CustomerManagementApplicationServiceTest(final CustomerManagementApplicationService service) {
        this.service = service;
    }

    @Test
    void shouldRegister(){
        final var input = CustomerInputDataBuilder.builder().build();
        final var actual = service.create(input);
        assertThat(actual).isNotNull();
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

}