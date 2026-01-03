package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class BirthDateTest {

    private static final CustomFaker faker = CustomFaker.getInstance();

    @Test
    void shouldCreateBirthDate() {
        final var value = faker.timeAndDate().birthday();
        var birthdate = new BirthDate(value);
        assertThat(birthdate.age()).isEqualTo(Period.between(value, LocalDate.now()).getYears());
    }

    private static final List<Arguments> shouldNotCreateBirthDate =
            List.of(
                    Arguments.of(LocalDate.now().plusDays(1), IllegalArgumentException.class),
                    Arguments.of(null, NullPointerException.class)
            );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreateBirthDate(final LocalDate value, final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new BirthDate(value));
    }

}