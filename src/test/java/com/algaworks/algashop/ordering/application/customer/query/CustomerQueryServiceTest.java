package com.algaworks.algashop.ordering.application.customer.query;

import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerInputDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@SpringBootTest
class CustomerQueryServiceTest extends AbstractApplicationTest {

    private final CustomerManagementApplicationService service;
    private final CustomerQueryService queryService;

    @Autowired
    CustomerQueryServiceTest(final JdbcTemplate jdbcTemplate,
                             final CustomerManagementApplicationService service,
                             final CustomerQueryService queryService) {
        super(jdbcTemplate);
        this.service = service;
        this.queryService = queryService;
    }


    @Test
    void shouldFindById(){
        final var input = CustomerInputDataBuilder.builder().build();
        final var id = service.create(input);
        final var actual = queryService.findById(id);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id", "registeredAt", "archived", "archivedAt", "loyaltyPoints")
                .isEqualTo(input);
    }

    @Test
    void givenNonStoredIdWhenFindByIdThenThrowException(){
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> queryService.findById(UUID.randomUUID()));
    }

}