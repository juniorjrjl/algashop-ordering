package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@UnitTest
class QuantityTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldCreate() {
        final var value = customFaker.number().numberBetween(0, Integer.MAX_VALUE);
        assertThat(new Quantity(value).value()).isEqualTo(value);
    }

    private static final List<Arguments> shouldNotCreate = List.of(
            Arguments.of(customFaker.number().numberBetween(Integer.MIN_VALUE, -1), IllegalArgumentException.class),
            Arguments.of(null, NullPointerException.class));

    @ParameterizedTest
    @FieldSource
    void shouldNotCreate(final Integer value, final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException).isThrownBy(() -> new Quantity(value));
    }

    @Test
    void givenTwoQuantityObjectsWhenSumBothThenReturnValue(){
        final var money1 = customFaker.valueObject().quantity(1, 20);
        final var value1 = money1.value();
        final var money2 = customFaker.valueObject().quantity(1, 20);
        final var value2 = money2.value();
        assertThat(money1.add(money2).value()).isEqualTo(value1 + value2);
    }

    @Test
    void giveGreaterValueWhenCheckIsLessThenReturnTrue(){
        final var quantity1 = customFaker.valueObject().quantity(1, 10);
        final var quantity2 = customFaker.valueObject().quantity(11, 20);
        assertThat(quantity1.isLessThan(quantity2)).isTrue();
    }

    @Test
    void giveLessValueWhenCheckIsGreaterThenReturnTrue(){
        final var quantity1 = customFaker.valueObject().quantity(11, 20);
        final var quantity2 = customFaker.valueObject().quantity(1, 10);
        assertThat(quantity1.isGreaterThan(quantity2)).isTrue();
    }

    @Test
    void giveSameValueWhenCheckIsEqualThenReturnTrue(){
        final var value = customFaker.number().numberBetween(0, Integer.MAX_VALUE);
        assertThat(new Quantity(value).equals(new Quantity(value))).isTrue();
    }

}