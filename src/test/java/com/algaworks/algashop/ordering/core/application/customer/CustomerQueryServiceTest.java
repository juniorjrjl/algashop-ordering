package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.port.in.customer.CustomerFilter;
import com.algaworks.algashop.ordering.core.port.in.customer.ForQueryingCustomer;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.core.port.in.customer.ForManagingCustomer;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerInputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import static com.algaworks.algashop.ordering.core.port.in.customer.CustomerFilter.SortType.FIRST_NAME;
import static com.algaworks.algashop.ordering.core.port.in.customer.CustomerFilter.SortType.REGISTERED_AT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
class CustomerQueryServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final ForManagingCustomer service;
    private final ForQueryingCustomer queryService;
    private final Customers customers;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    CustomerQueryServiceTest(final ForManagingCustomer service,
                             final ForQueryingCustomer queryService,
                             final Customers customers) {
        this.service = service;
        this.queryService = queryService;
        this.customers = customers;
    }

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
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