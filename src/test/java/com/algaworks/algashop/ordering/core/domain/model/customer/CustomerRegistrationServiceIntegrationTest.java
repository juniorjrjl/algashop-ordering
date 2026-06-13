package com.algaworks.algashop.ordering.core.domain.model.customer;

import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerRegistrationService;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
class CustomerRegistrationServiceIntegrationTest {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    private final CustomerRegistrationService service;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    CustomerRegistrationServiceIntegrationTest(final CustomerRegistrationService service) {
        this.service = service;
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

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
    }

    @Test
    void shouldRegister(){
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
