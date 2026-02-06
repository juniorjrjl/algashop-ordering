package com.algaworks.algashop.ordering.utility;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.util.Optional;
import java.util.Random;

@Slf4j
public class CustomFaker extends Faker {

    private static Optional<CustomFaker> optionalCustomFaker = Optional.empty();

    private CustomFaker() {
        final var random = new Random();
        final var randomSeed = random.nextLong();
        log.info("Random seed: {}", randomSeed);
        super(new Random(randomSeed));
    }

    public static CustomFaker getInstance() {
        if (optionalCustomFaker.isEmpty()){
            optionalCustomFaker = Optional.of(new CustomFaker());
        }
        return optionalCustomFaker.get();
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
