package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.application.common.AddressData;
import net.datafaker.providers.base.AbstractProvider;

public class CommonApplicationProvider extends AbstractProvider<CustomFaker> {

    protected CommonApplicationProvider(final CustomFaker faker) {
        super(faker);
    }


    public AddressData address(){
        return AddressData.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .neighborhood(faker.lorem().characters())
                .build();
    }

    public AddressData addressWithComplement(){
        return AddressData.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .neighborhood(faker.lorem().characters())
                .complement(faker.address().buildingNumber())
                .build();
    }

}
