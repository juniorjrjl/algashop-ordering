package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
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
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssemblerImpl.class,
        CustomerPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
class CustomersTest {

    private final static CustomFaker customFaker = CustomFaker.getInstance();

    private final Customers customers;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    CustomersTest(final Customers customers) {
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
    void shouldPersistAndFind(){
        final var customer = CustomerDataBuilder.builder()
                .buildExisting();
        customers.add(customer);
        final var optional = customers.ofId(customer.id());
        assertThat(optional).isPresent();
        final var actual = optional.get();
        assertThat(actual)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(OffsetDateTime::toInstant), OffsetDateTime.class)
                .isEqualTo(customer);
    }

    @Test
    void shouldUpdateExistingCustomer(){
        final var customer = CustomerDataBuilder.builder()
                .withArchived(() -> false)
                .buildExisting();
        customers.add(customer);
        final var storedCustomer = customers.ofId(customer.id()).orElseThrow();
        final var newAddress = customFaker.common().addressWithComplement();
        storedCustomer.changeAddress(newAddress);
        customers.add(storedCustomer);
        final var actual = customers.ofId(storedCustomer.id()).orElseThrow();
        assertThat(actual.address()).isEqualTo(newAddress);
    }

    @Test
    void shouldNotAllowStaleUpdates(){
        final var customer = CustomerDataBuilder.builder()
                .withPromotionNotificationsAllowed(() -> false)
                .withArchived(() -> false)
                .buildExisting();
        customers.add(customer);

        final var firstSearch = customers.ofId(customer.id()).orElseThrow();
        final var secondSearch = customers.ofId(customer.id()).orElseThrow();

        firstSearch.enablePromotionNotifications();
        customers.add(firstSearch);

        secondSearch.disablePromotionNotifications();
        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> customers.add(secondSearch));

        final var storedOrder = customers.ofId(customer.id()).orElseThrow();
        assertThat(storedOrder.isPromotionNotificationsAllowed()).isTrue();
    }

    @Test
    void shouldCountExistingOrders(){
        assertThat(customers.count()).isZero();
        final var toInsert = Stream.generate(() -> CustomerDataBuilder.builder().buildExisting())
                .limit(customFaker.number().numberBetween(1, 10))
                .collect(Collectors.toSet());
        toInsert.forEach(customers::add);
        assertThat(customers.count()).isEqualTo(toInsert.size());
    }

    @Test
    void shouldReturnIfOrdersExist(){
        final var customer = CustomerDataBuilder.builder().buildExisting();
        customers.add(customer);
        assertThat(customers.exists(customer.id())).isTrue();
    }

    @Test
    void shouldReturnIfOrdersNotExist(){
        assertThat(customers.exists(new CustomerId())).isFalse();
    }

    @Test
    void shouldFoundByEmail(){
        final var customer = CustomerDataBuilder.builder().buildExisting();
        customers.add(customer);
        assertThat(customers.ofEmail(customer.email())).isPresent();
    }

    @Test
    void shouldNotFoundByEmail(){
        final var customer = CustomerDataBuilder.builder().buildExisting();
        assertThat(customers.ofEmail(customer.email())).isEmpty();
    }

    @Test
    void shouldReturnEmailNotInUse() {
        final var customer = CustomerDataBuilder.builder().buildExisting();
        customers.add(customer);

        assertThat(customers.isEmailUnique(customer.email(), customer.id())).isTrue();
    }

    @Test
    void shouldReturnEmailInUse(){
        final var customer = CustomerDataBuilder.builder().buildExisting();
        customers.add(customer);

        assertThat(customers.isEmailUnique(customer.email(), new CustomerId())).isFalse();
    }

    @Test
    void givenNewCustomerWithNonStoredEmailShouldReturnEmailNotInUse() {
        final var stored = CustomerDataBuilder.builder().buildExisting();
        customers.add(stored);

        final var customer = CustomerDataBuilder.builder().buildExisting();
        assertThat(customers.isEmailUnique(customer.email(), customer.id())).isTrue();
    }

}
