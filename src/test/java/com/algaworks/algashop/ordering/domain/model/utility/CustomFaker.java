package com.algaworks.algashop.ordering.domain.model.utility;

import net.datafaker.Faker;

public class CustomFaker extends Faker {

    public ValueObjectProvider valueObject(){
        return getProvider(ValueObjectProvider.class, ValueObjectProvider::new);
    }

}
