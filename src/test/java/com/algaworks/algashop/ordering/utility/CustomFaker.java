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

    public ValueObjectProvider valueObject(){
        return getProvider(ValueObjectProvider.class, ValueObjectProvider::new);
    }

}
