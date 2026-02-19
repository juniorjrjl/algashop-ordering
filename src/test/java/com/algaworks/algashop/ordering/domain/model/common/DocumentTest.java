package com.algaworks.algashop.ordering.domain.model.common;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@UnitTest
class DocumentTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
    }

    @Test
    void shouldCreateDocument(){
        final var value = customFaker.cpf().valid();
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