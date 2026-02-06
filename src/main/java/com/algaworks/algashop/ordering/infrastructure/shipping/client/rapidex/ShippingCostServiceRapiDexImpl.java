package com.algaworks.algashop.ordering.infrastructure.shipping.client.rapidex;

import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@ConditionalOnProperty(name = "algashop.integrations.shipping.provider", havingValue = "RAPIDEX")
@RequiredArgsConstructor
public class ShippingCostServiceRapiDexImpl implements ShippingCostService {

    private final RapiDexAPIClient rapiDexAPIClient;

    @Override
    public CalculationResult calculate(final CalculationRequest request) {
        final var response = rapiDexAPIClient.calculate(
                new DeliveryCostRequest(request.origin().value(), request.destination().value())
        );
        final var expectedDeliveryDate = LocalDate.now().plusDays(response.getEstimatedDaysToDeliver());
        return CalculationResult.builder()
                .cost(new Money(response.getDeliveryCost()))
                .expectedDate(expectedDeliveryDate)
                .build();
    }
}
