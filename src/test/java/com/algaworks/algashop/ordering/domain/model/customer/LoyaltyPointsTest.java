package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@UnitTest
class LoyaltyPointsTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldGenerate(){
        final var value = customFaker.number().numberBetween(1, 20);
        final var loyaltyPoints = new LoyaltyPoints(value);
        assertThat(loyaltyPoints.value()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    void shouldNotGenerate(final Integer invalidValue){
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new LoyaltyPoints(invalidValue));
    }


    @Test
    void shouldAddValue(){
        final var value = customFaker.number().numberBetween(1, 20);
        final var toAdd = customFaker.number().numberBetween(1, 20);
        final var loyaltyPoints = new LoyaltyPoints(value);
        final var updatedLoyaltyPoints = loyaltyPoints.add(toAdd);
        assertThat(updatedLoyaltyPoints.value()).isEqualTo(value + toAdd);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    void shouldNotAddValue(final Integer invalidValue){
        final var value = customFaker.number().numberBetween(1, 20);
        final var loyaltyPoints = new LoyaltyPoints(value);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> loyaltyPoints.add(invalidValue));
        assertThat(loyaltyPoints.value()).isEqualTo(value);
    }

}