package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import lombok.Builder;

import java.time.LocalDate;

public interface ShippingCostService {

    CalculationResult calculate(final CalculationRequest request);

    @Builder
    record CalculationRequest(ZipCode origin, ZipCode destination){}

    @Builder
    record CalculationResult(Money cost, LocalDate expectedDate){}

}
