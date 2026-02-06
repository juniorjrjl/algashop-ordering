package com.algaworks.algashop.ordering.domain.model.common;

import com.algaworks.algashop.ordering.domain.model.commons.Phone;
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
class PhoneTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldCreatePhone(){
        final var value = customFaker.phoneNumber().cellPhone();
        final var document = new Phone(value);
        assertThat(document.toString()).hasToString(value);
    }

    private static final List<Arguments> shouldNotCreatePhone =
            List.of(
                    Arguments.of(" ", IllegalArgumentException.class),
                    Arguments.of(null, NullPointerException.class)
            );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreatePhone(final String value, final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new Phone(value));
    }

}