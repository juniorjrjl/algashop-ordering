package com.algaworks.algashop.ordering.application.customer.query;

import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerInputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import static com.algaworks.algashop.ordering.application.customer.query.CustomerFilter.SortType.FIRST_NAME;
import static com.algaworks.algashop.ordering.application.customer.query.CustomerFilter.SortType.REGISTERED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@SpringBootTest
class CustomerQueryServiceTest extends AbstractApplicationTest {

    private final CustomerManagementApplicationService service;
    private final CustomerQueryService queryService;
    private final Customers customers;

    @Autowired
    CustomerQueryServiceTest(final JdbcTemplate jdbcTemplate,
                             final CustomerManagementApplicationService service,
                             final CustomerQueryService queryService,
                             final Customers customers) {
        super(jdbcTemplate);
        this.service = service;
        this.queryService = queryService;
        this.customers = customers;
    }

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
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

    @Test
    void shouldFindByFistNameStartsWith(){
        final var customersToInsert = CustomerDataBuilder.builder()
                .buildExistingList(customFaker.number().numberBetween(5, 10));
        customersToInsert.forEach(customers::add);
        final var charToSearch = customersToInsert.stream()
                .map(c -> c.fullName().firstName())
                .map(f -> String.valueOf(f.charAt(0)))
                .toList().get(customFaker.number().numberBetween(0, customersToInsert.size()));
        final var filter = new CustomerFilter(0, 10);
        filter.setFirstName(charToSearch);
        final var actual = queryService.filter(filter);
        assertThat(actual.getContent()).isNotEmpty();
        assertThat(actual.getContent()).
                extracting(CustomerSummaryOutput::getFirstName)
                .allSatisfy(name -> assertThat(name).startsWithIgnoringCase(charToSearch));
    }

    @Test
    void shouldFindByEmailLike(){
        final var customersToInsert = CustomerDataBuilder.builder()
                .buildExistingList(customFaker.number().numberBetween(5, 10));
        customersToInsert.forEach(customers::add);
        final var charToSearch = customersToInsert.stream()
                .map(c -> c.email().value())
                .map(f -> Arrays.asList(f.split("")))
                .flatMap(Collection::stream)
                .toList().get(customFaker.number().numberBetween(0, customersToInsert.size()));
        final var filter = new CustomerFilter(0, 10);
        filter.setEmail(charToSearch);
        final var actual = queryService.filter(filter);
        assertThat(actual.getContent()).isNotEmpty();
        assertThat(actual.getContent()).
                extracting(CustomerSummaryOutput::getEmail)
                .allSatisfy(email -> assertThat(email).containsIgnoringCase(charToSearch));
    }

    @Test
    void shouldFindByEmailLikeAndNameStartsWith(){
        final var customersToInsert = CustomerDataBuilder.builder()
                .buildExistingList(customFaker.number().numberBetween(5, 10));
        customersToInsert.forEach(customers::add);
        final var selectedCustomer = customersToInsert.stream()
                .toList()
                .get(customFaker.number().numberBetween(0, customersToInsert.size()));
        final var nameChar = String.valueOf(selectedCustomer.fullName().firstName().charAt(0));
        final var emailChar = Arrays.asList(selectedCustomer.email().value().split(""))
                .get(customFaker.number().numberBetween(0, selectedCustomer.email().value().length()));
        final var filter = new CustomerFilter(0, 10);
        filter.setFirstName(nameChar);
        filter.setEmail(emailChar);
        final var actual = queryService.filter(filter);
        assertThat(actual.getContent()).isNotEmpty();
        assertThat(actual.getContent())
                .allSatisfy(c -> {
                    assertThat(c.getEmail()).containsIgnoringCase(emailChar);
                    assertThat(c.getFirstName()).startsWithIgnoringCase(nameChar);
                });
    }

    @Test
    void shouldReturnPageInfo(){
        final var customersToInsert = CustomerDataBuilder.builder()
                .buildExistingList(customFaker.number().numberBetween(5, 10));
        customersToInsert.forEach(customers::add);
        final var filter = new CustomerFilter(0, 3);
        final var actual = queryService.filter(filter);
        final var isOdd = customersToInsert.size() % filter.getSize() != 0;
        int totalPages = customersToInsert.size() / filter.getSize() + (isOdd? 1 : 0);
        assertThat(actual.getTotalPages()).isEqualTo(totalPages);
        assertThat(actual.getTotalElements()).isEqualTo(customersToInsert.size());
        assertThat(actual.getNumberOfElements()).isEqualTo(filter.getSize());
    }

    private static Stream<Arguments> shouldSortBy(){
        return Stream.of(
                Arguments.of(
                        ASC,
                        REGISTERED_AT,
                        Comparator.comparing(CustomerSummaryOutput::getRegisteredAt)
                ),
                Arguments.of(
                        ASC,
                        FIRST_NAME,
                        Comparator.comparing(CustomerSummaryOutput::getFirstName)
                ),
                Arguments.of(
                        DESC,
                        REGISTERED_AT,
                        Comparator.comparing(CustomerSummaryOutput::getRegisteredAt).reversed()
                ),
                Arguments.of(
                        DESC,
                        FIRST_NAME,
                        Comparator.comparing(CustomerSummaryOutput::getFirstName).reversed()
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldSortBy(final Sort.Direction direction,
                      final CustomerFilter.SortType sortType,
                      final Comparator<CustomerSummaryOutput> comparator){
        final var customersToInsert = CustomerDataBuilder.builder()
                .buildExistingList(customFaker.number().numberBetween(5, 10));
        customersToInsert.forEach(customers::add);
        final var filter = new CustomerFilter(0, 10);
        filter.setSortDirection(direction);
        filter.setSortByProperty(sortType);
        final var actual = queryService.filter(filter);
        assertThat(actual.getContent()).isNotEmpty();
        assertThat(actual.getContent()).isSortedAccordingTo(comparator);
    }

    @Test
    void givenNonStoredCustomerWhenFilterThenReturnEmptyPage(){
        final var filter = new CustomerFilter(0, 10);
        final var actual = queryService.filter(filter);
        assertThat(actual.getContent()).isEmpty();
        assertThat(actual.getTotalElements()).isZero();
        assertThat(actual.getNumberOfElements()).isZero();
        assertThat(actual.getTotalPages()).isZero();
    }

}