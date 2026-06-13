package com.algaworks.algashop.ordering.core.domain.model.order.shipping;

import com.algaworks.algashop.ordering.core.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.infrastructure.DeliveryCostResponseDataBuilder;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@IntegrationTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
@ActiveProfiles("test")
@EnableWireMock({@ConfigureWireMock(name = "rapiDexApi", port = 8780)})
class ShippingCostServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final ShippingCostService shippingCostService;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    ShippingCostServiceTest(final ShippingCostService shippingCostService) {
        this.shippingCostService = shippingCostService;
    }

    @Test
    void shouldCalculate() throws JsonProcessingException {
        final var response = DeliveryCostResponseDataBuilder.builder().buildJson();
        stubFor(post("/api/delivery-cost").willReturn(okJson(response)));
        final var origin = customFaker.common().zipCode();
        final var destination = customFaker.common().zipCode();

        final var request = new ShippingCostService.CalculationRequest(destination, origin);
        final var actual = shippingCostService.calculate(request);
        assertThat(actual).hasNoNullFieldsOrProperties();
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

}