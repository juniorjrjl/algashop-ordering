package com.algaworks.algashop.ordering.domain.model.common;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
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
class EmailTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldCreateEmail(){
        final var value = customFaker.internet().safeEmailAddress();
        final var email = new Email(value);
        assertThat(email.toString()).hasToString(value);
    }

    private static final List<Arguments> shouldNotCreateEmail =
            List.of(
                    Arguments.of(customFaker.lorem().word(), IllegalArgumentException.class),
                    Arguments.of(null, NullPointerException.class)
            );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreateEmail(final String value, final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new Email(value));
    }

}