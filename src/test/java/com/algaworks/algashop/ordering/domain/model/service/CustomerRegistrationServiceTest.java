package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    @Mock
    private Customers customers;

    @InjectMocks
    private CustomerRegistrationService service;

    @Test
    void shouldRegister(){
        when(customers.isEmailUnique(any(Email.class), any(CustomerId.class))).thenReturn(true);
        final var fullName = customFaker.valueObject().fullName();
        final var birthDate = customFaker.valueObject().birthDate();
        final var email = customFaker.valueObject().email();
        final var phone = customFaker.valueObject().phone();
        final var document = customFaker.valueObject().document();
        final var promotionNotificationsAllowed = customFaker.bool().bool();
        final var address = customFaker.valueObject().addressWithComplement();
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
