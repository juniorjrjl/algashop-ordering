package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.Specification;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.infrastructure.config.FreeShippingConfig;

public class CustomerHaveFreeShippingSpecification implements Specification<Customer> {


    private final CustomerHasOrderedEnougtAtYearSpecification hasOrderedEnoughAtYear;
    private final CustomerHasEnoughLoyaltyPointsSpecification hasEnoughBasicLoyaltyPoints;
    private final CustomerHasEnoughLoyaltyPointsSpecification hasEnoughPremiumLoyaltyPoints;


    public CustomerHaveFreeShippingSpecification(final Orders orders,
                                                 final FreeShippingConfig freeShippingConfig) {
        this.hasOrderedEnoughAtYear = new CustomerHasOrderedEnougtAtYearSpecification(orders, freeShippingConfig.getMinBasicSalesAmountInYear());
        this.hasEnoughBasicLoyaltyPoints = new CustomerHasEnoughLoyaltyPointsSpecification(freeShippingConfig.getMinBasicLoyaltyPoints());
        this.hasEnoughPremiumLoyaltyPoints = new CustomerHasEnoughLoyaltyPointsSpecification(freeShippingConfig.getMinPremiumLoyaltyPoints());
    }


    @Override
    public boolean isSatisfiedBy(final Customer customer) {
        return hasEnoughBasicLoyaltyPoints
                .and(hasOrderedEnoughAtYear)
                .or(hasEnoughPremiumLoyaltyPoints)
                .isSatisfiedBy(customer);
    }

}
