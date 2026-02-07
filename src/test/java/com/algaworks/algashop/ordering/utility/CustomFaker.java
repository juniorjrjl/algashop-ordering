package com.algaworks.algashop.ordering.utility;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.util.Random;

import static java.util.Objects.isNull;

@Slf4j
public class CustomFaker extends Faker {

    private static CustomFaker optionalCustomFaker = null;

    private CustomFaker() {
        final var random = new Random();
        final var randomSeed = random.nextLong();
        log.info("Random seed: {}", randomSeed);
        super(new Random(randomSeed));
    }

    public static CustomFaker getInstance() {
        if (isNull(optionalCustomFaker)){
            optionalCustomFaker = new CustomFaker();
        }
        return optionalCustomFaker;
    }

    public CommonApplicationProvider commonApplication(){
        return getProvider(CommonApplicationProvider.class, CommonApplicationProvider::new);
    }

    public CommonProvider common(){
        return getProvider(CommonProvider.class, CommonProvider::new);
    }

    public CustomerProvider customer(){
        return getProvider(CustomerProvider.class, CustomerProvider::new);
    }

    public OrderProvider order(){
        return getProvider(OrderProvider.class, OrderProvider::new);
    }

    public ProductProvider  product(){
        return getProvider(ProductProvider.class, ProductProvider::new);
    }

}
