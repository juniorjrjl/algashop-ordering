package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_EVEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MoneyTest {

    private static final CustomFaker customFaker = new CustomFaker();

    private static final List<BigDecimal> shouldCreateWithBigDecimal = List.of(
            new BigDecimal(Double.toString(customFaker.number().randomDouble(9,1, 1000))),
            new BigDecimal(Double.toString(customFaker.number().randomDouble(2,1, 1000)))
    );

    @ParameterizedTest
    @FieldSource
    void shouldCreateWithBigDecimal(final BigDecimal value) {
        final var money = new Money(value);
        assertThat(money.value()).isEqualTo(value.setScale(2, HALF_EVEN));
    }

    private static final List<Arguments> shouldNotCreateWithBigDecimal = List.of(
            Arguments.of(
                    new BigDecimal(customFaker.number().randomDouble(2, Integer.MIN_VALUE, -1)),
                    IllegalArgumentException.class
            ),
            Arguments.of(null, NullPointerException.class)
    );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreateWithBigDecimal(final BigDecimal value, final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new Money(value));
    }

    private static final List<String> shouldCreateWithString = List.of(
            Double.toString(customFaker.number().randomDouble(9,1, 1000)),
            Double.toString(customFaker.number().randomDouble(2,1, 1000))
    );

    @ParameterizedTest
    @FieldSource
    void shouldCreateWithString(final String value) {
        final var money = new Money(value);
        assertThat(money.value()).isEqualTo(new BigDecimal(value).setScale(2, HALF_EVEN));
    }

    private static final List<Arguments> shouldNotCreateWithString = List.of(
            Arguments.of(customFaker.lorem().word(), IllegalArgumentException.class),
            Arguments.of(null, NullPointerException.class)
    );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreateWithString(final String value, final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new Money(value));
    }

    @Test
    void givenTwoMoneyObjectsWhenSumBothThenReturnValue(){
        final var money1 = customFaker.valueObject().money();
        final var value1 = money1.value();
        final var money2 = customFaker.valueObject().money();
        final var value2 = money2.value();
        assertThat(money1.add(money2).value()).isEqualTo(value1.add(value2));
    }

    @Test
    void givenTwoMoneyObjectsWhenMultiplyBothThenReturnValue(){
        final var money = customFaker.valueObject().money();
        final var value1 = money.value();
        final var quantity = customFaker.valueObject().quantity(1, 10);
        final var value2 = quantity.value();
        assertThat(money.multiply(quantity).value())
                .isEqualTo(value1.multiply(new BigDecimal(value2)).setScale(2, HALF_EVEN));
    }

    @Test
    void givenTwoMoneyObjectsWhenDivideBothThenReturnValue(){
        final var money1 = customFaker.valueObject().money();
        final var value1 = money1.value();
        final var money2 = customFaker.valueObject().money(1, 2);
        final var value2 = money2.value();
        assertThat(money1.divide(money2).value()).isEqualTo(value1.divide(value2, 2, HALF_EVEN));
    }

    @Test
    void giveGreaterValueWhenCheckIsLessThenReturnTrue(){
        final var money1 = customFaker.valueObject().money(1, 10);
        final var money2 = customFaker.valueObject().money(11, 20);
        assertThat(money1.isLessThan(money2)).isTrue();
    }

    @Test
    void giveLessValueWhenCheckIsGreaterThenReturnTrue(){
        final var money1 = customFaker.valueObject().money(11, 20);
        final var money2 = customFaker.valueObject().money(1, 10);
        assertThat(money1.isGreaterThan(money2)).isTrue();
    }

    @Test
    void giveSameValueWhenCheckIsEqualThenReturnTrue(){
        final var value = customFaker.number().randomDouble(2, 1, 100);
        final var stringValue = Double.toString(value);
        final var bigDecimalValue = new BigDecimal(stringValue);
        assertThat(new Money(stringValue).equals(new Money(bigDecimalValue))).isTrue();
    }

}