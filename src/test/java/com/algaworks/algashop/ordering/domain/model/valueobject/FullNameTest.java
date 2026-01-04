package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.tag.UnitTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;

@UnitTest
class FullNameTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private static final List<Arguments> shouldCreate = List.of(
            Arguments.of(customFaker.name().firstName(), customFaker.name().lastName()),
            Arguments.of(" "+ customFaker.name().firstName(), " "+ customFaker.name().lastName()),
            Arguments.of(customFaker.name().firstName() + " ", customFaker.name().lastName() + " ")
    );

    @ParameterizedTest
    @FieldSource
    void shouldCreate(final String firstName, final String lastName) {
        var fullName = new FullName(firstName, lastName);
        assertWith(fullName,
                f -> assertThat(f.firstName()).isEqualTo(firstName.trim()),
                f -> assertThat(f.lastName()).isEqualTo(lastName.trim()),
                f -> assertThat(f.toString()).hasToString(firstName.trim() + " " + lastName.trim())
        );
    }

    private static final List<Arguments> shouldNotCreate = List.of(
            Arguments.of(customFaker.name().firstName(), "", IllegalArgumentException.class),
            Arguments.of(customFaker.name().firstName(), " ", IllegalArgumentException.class),
            Arguments.of(customFaker.name().firstName(), null, NullPointerException.class),
            Arguments.of("", customFaker.name().lastName(), IllegalArgumentException.class),
            Arguments.of(" ", customFaker.name().lastName(), IllegalArgumentException.class),
            Arguments.of(null, customFaker.name().lastName(), NullPointerException.class)
    );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreate(final String firstName,
                         final String lastName,
                         final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new FullName(firstName, lastName));
    }

}
