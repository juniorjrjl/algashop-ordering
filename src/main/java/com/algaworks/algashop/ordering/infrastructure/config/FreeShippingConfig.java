package com.algaworks.algashop.ordering.infrastructure.config;

import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties("algashop.free-shipping")
@Getter
public class FreeShippingConfig {

    private final LoyaltyPoints minBasicLoyaltyPoints;
    private final Integer minBasicSalesAmountInYear;
    private final LoyaltyPoints minPremiumLoyaltyPoints;

    @ConstructorBinding
    public FreeShippingConfig(final Integer minBasicLoyaltyPoints,
                              final Integer minBasicSalesAmountInYear,
                              final Integer minPremiumLoyaltyPoints) {
        this.minBasicLoyaltyPoints = new LoyaltyPoints(minBasicLoyaltyPoints);
        this.minBasicSalesAmountInYear = minBasicSalesAmountInYear;
        this.minPremiumLoyaltyPoints = new LoyaltyPoints(minPremiumLoyaltyPoints);
    }


}
