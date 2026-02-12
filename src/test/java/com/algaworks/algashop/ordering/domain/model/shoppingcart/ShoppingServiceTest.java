package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.utility.MockitoWithResetExtension;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@UnitTest
@ExtendWith(MockitoWithResetExtension.class)
class ShoppingServiceTest {

    @Mock
    private Customers customers;
    @Mock
    private ShoppingCarts shoppingCarts;

    @InjectMocks
    private ShoppingService service;

    @Test
    void givenNullableCustomerIdWhenStartShoppingThenThrowException() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> service.startShopping(null));
        verifyNoInteractions(customers, shoppingCarts);
    }

    @Test
    void givenCustomerIdNonStoredWhenStartShoppingThenThrowException() {
        final var customerId = new CustomerId();
        when(customers.exists(customerId)).thenReturn(false);
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.startShopping(customerId));
        verifyNoInteractions(shoppingCarts);
    }

    @Test
    void givenCustomerWithActiveShoppingCartWhenStartShoppingThenThrowException() {
        final var customer = CustomerDataBuilder.builder().buildExisting();
        final var customerId = customer.id();
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withCustomerId(() -> customerId)
                .build();
        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.of(shoppingCart));
        assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> service.startShopping(customerId));
    }

    @Test
    void givenCustomerWithoutActiveShoppingCartWhenStartShoppingThenThrowException() {
        final var customer = CustomerDataBuilder.builder().buildExisting();
        final var customerId = customer.id();
        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.empty());
        final var actual = service.startShopping(customerId);
        assertWith(actual,
                s -> assertThat(s.customerId()).isEqualTo(customerId),
                s -> assertThat(s.id()).isNotNull(),
                s -> assertThat(s.items()).isNotNull(),
                s -> assertThat(s.items()).isEmpty(),
                s -> assertThat(s.createdAt()).isNotNull()
                );
    }

}