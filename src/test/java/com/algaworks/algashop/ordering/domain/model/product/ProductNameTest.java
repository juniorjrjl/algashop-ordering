package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@UnitTest
class ProductNameTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
    }

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
