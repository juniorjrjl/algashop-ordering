package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.infrastructure.DeliveryCostResponseDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@IntegrationTest
@EnableWireMock({@ConfigureWireMock(name = "rapiDexApi", port = 8780)})
class ShippingCostServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;

    @Autowired
    ShippingCostServiceTest(final ShippingCostService shippingCostService,
                            final OriginAddressService originAddressService) {
        this.shippingCostService = shippingCostService;
        this.originAddressService = originAddressService;
    }

    @Test
    void shouldCalculate() throws JsonProcessingException {
        final var response = DeliveryCostResponseDataBuilder.builder().buildJson();
        stubFor(post("/api/delivery-cost").willReturn(okJson(response)));
        final var origin = customFaker.valueObject().zipCode();
        final var destination = customFaker.valueObject().zipCode();

        final var request = new ShippingCostService.CalculationRequest(destination, origin);
        final var actual = shippingCostService.calculate(request);
        assertThat(actual).hasNoNullFieldsOrProperties();
    }

}