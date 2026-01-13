package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssemblerImpl.class,
        CustomerPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class
})
class CustomersTest extends AbstractDBTest {

    private final Customers customers;

    @Autowired
    CustomersTest(final Customers customers, final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.customers = customers;
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
                .isEqualTo(customer);
    }

    @Test
    void shouldUpdateExistingCustomer(){
        final var customer = CustomerDataBuilder.builder()
                .withArchived(() -> false)
                .buildExisting();
        customers.add(customer);
        final var storedCustomer = customers.ofId(customer.id()).orElseThrow();
        final var newAddress = customFaker.valueObject().addressWithComplement();
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
