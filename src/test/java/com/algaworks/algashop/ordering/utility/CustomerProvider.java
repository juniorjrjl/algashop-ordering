package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class CustomerProvider extends AbstractProvider<CustomFaker> {

    protected CustomerProvider(final CustomFaker faker) {
        super(faker);
    }


    public LoyaltyPoints loyaltyPoints() {
        return loyaltyPoints(1,100);
    }

    public LoyaltyPoints loyaltyPoints(final int min, final int max) {
        return new LoyaltyPoints(faker.number().numberBetween(min, max));
    }

    public BirthDate birthDate(){
        return new BirthDate(faker.timeAndDate().birthday());
    }

    public BirthDate birthDate(int minAge, int maxAge) {
        return new BirthDate(faker.timeAndDate().birthday(minAge, maxAge));
    }



}
