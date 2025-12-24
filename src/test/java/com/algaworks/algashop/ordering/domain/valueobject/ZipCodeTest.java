package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class ZipCodeTest {

    private static final CustomFaker faker = new CustomFaker();

    @Test
    void shouldCreateZipCode() {
        final var value = faker.address().zipCode();
        final var zipCode = new ZipCode(value);
        assertThat(zipCode.toString()).hasToString(value);
    }

    private static final List<Arguments> shouldNotCreateZipCode = List.of(
            Arguments.of("", IllegalArgumentException.class),
            Arguments.of("123456", IllegalArgumentException.class),
            Arguments.of(null, NullPointerException.class),
            Arguments.of("", IllegalArgumentException.class)
    );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreateZipCode(final String value, final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new ZipCode(value));
    }

}
