package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LoyaltyPointsTest {

    private static final CustomFaker faker = new CustomFaker();

    @Test
    void shouldGenerate(){
        final var value = faker.number().numberBetween(1, 20);
        final var loyaltyPoints = new LoyaltyPoints(value);
        assertThat(loyaltyPoints.value()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    @NullSource
    void shouldNotGenerate(final Integer invalidValue){
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new LoyaltyPoints(invalidValue));
    }


    @Test
    void shouldAddValue(){
        final var value = faker.number().numberBetween(1, 20);
        final var toAdd = faker.number().numberBetween(1, 20);
        final var loyaltyPoints = new LoyaltyPoints(value);
        final var updatedLoyaltyPoints = loyaltyPoints.add(toAdd);
        assertThat(updatedLoyaltyPoints.value()).isEqualTo(value + toAdd);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    @NullSource
    void shouldNotAddValue(final Integer invalidValue){
        final var value = faker.number().numberBetween(1, 20);
        final var loyaltyPoints = new LoyaltyPoints(value);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> loyaltyPoints.add(invalidValue));
        assertThat(loyaltyPoints.value()).isEqualTo(value);
    }

}