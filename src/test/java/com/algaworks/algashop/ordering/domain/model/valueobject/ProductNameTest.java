package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ProductNameTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private static final List<Arguments> shouldCreate = List.of(
            Arguments.of(customFaker.lorem().word()),
            Arguments.of(" "+ customFaker.lorem().word()),
            Arguments.of(customFaker.lorem().word() + " ")
    );

    @ParameterizedTest
    @FieldSource
    void shouldCreate(final String value) {
        var productName = new ProductName(value);
        assertThat(productName.toString()).hasToString(value.trim());
    }

    private static final List<Arguments> shouldNotCreate = List.of(
            Arguments.of("", IllegalArgumentException.class),
            Arguments.of(" ", IllegalArgumentException.class),
            Arguments.of(null, NullPointerException.class)
    );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreate(final String value,
                         final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new ProductName(value));
    }

}
