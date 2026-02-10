package com.algaworks.algashop.ordering.utility;

import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static java.util.Objects.isNull;

@Slf4j
public class CustomFaker extends Faker {

    private static CustomFaker customFaker = null;

    private CustomFaker() {
        final var random = new Random();
        final var randomSeed = random.nextLong();
        log.info("Random seed: {}", randomSeed);
        super(new Random(randomSeed));
    }

    public static CustomFaker getInstance() {
        if (isNull(customFaker)){
            customFaker = new CustomFaker();
        }
        return customFaker;
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

    @SafeVarargs
    public final <E extends Enum<E>> E option(final Class<E> enumeration, final E... exceptedValues) {
        final var options = enumeration.getEnumConstants();
        final var expectedSet = new HashSet<>(Arrays.asList(exceptedValues));
        if (expectedSet.size() == options.length){
            throw new IllegalArgumentException("All elements in 'exceptedValues'");
        }
        final var values = new ArrayList<>(List.of(options));
        values.removeAll(expectedSet);
        return values.get(customFaker.random().nextInt(values.size()));
    }

}
