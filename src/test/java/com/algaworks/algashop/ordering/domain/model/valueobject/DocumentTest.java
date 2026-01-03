package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.exception.DomainException;
import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class DocumentTest {

    private static final CustomFaker faker = new CustomFaker();

    @Test
    void shouldCreateDocument(){
        final var value = faker.cpf().valid();
        final var document = new Document(value);
        assertThat(document.toString()).hasToString(value);
    }

    private static final List<Arguments> shouldNotCreateDocument =
            List.of(
                    Arguments.of(" ", IllegalArgumentException.class),
                    Arguments.of(null, NullPointerException.class)
            );

    @ParameterizedTest
    @FieldSource
    void shouldNotCreateDocument(final String value, final Class<? extends DomainException> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new Document(value));
    }

}