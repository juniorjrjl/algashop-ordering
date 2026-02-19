package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.MockitoWithResetExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoWithResetExtension.class)
class CustomerRegistrationServiceTest {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    @Mock
    private Customers customers;

    @InjectMocks
    private CustomerRegistrationService service;

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
    }

    @Test
    void shouldRegister(){
        when(customers.isEmailUnique(any(Email.class), any(CustomerId.class))).thenReturn(true);
        final var fullName = customFaker.common().fullName();
        final var birthDate = customFaker.customer().birthDate();
        final var email = customFaker.common().email();
        final var phone = customFaker.common().phone();
        final var document = customFaker.common().document();
        final var promotionNotificationsAllowed = customFaker.bool().bool();
        final var address = customFaker.common().addressWithComplement();
        final var customer = service.register(
                fullName,
                birthDate,
                email,
                phone,
                document,
                promotionNotificationsAllowed,
                address
        );
        assertWith(customer,
                u -> assertThat(u.fullName()).isEqualTo(fullName),
                u -> assertThat(u.birthDate()).isEqualTo(birthDate),
                u -> assertThat(u.email()).isEqualTo(email),
                u -> assertThat(u.phone()).isEqualTo(phone),
                u -> assertThat(u.document()).isEqualTo(document),
                u -> assertThat(u.isPromotionNotificationsAllowed()).isEqualTo(promotionNotificationsAllowed),
                u -> assertThat(u.address()).isEqualTo(address)
        );
    }

}
