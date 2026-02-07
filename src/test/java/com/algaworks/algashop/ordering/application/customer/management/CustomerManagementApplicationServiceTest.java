package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerInputDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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

}