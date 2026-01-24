package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@IntegrationTest
@SpringBootTest
class CustomerRegistrationServiceIntegrationTest {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    private final CustomerRegistrationService service;

    @Autowired
    CustomerRegistrationServiceIntegrationTest(final CustomerRegistrationService service) {
        this.service = service;
    }

    @Test
    void shouldRegister(){
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
