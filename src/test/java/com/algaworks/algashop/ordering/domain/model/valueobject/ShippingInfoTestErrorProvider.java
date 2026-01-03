package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;

public class ShippingInfoTestErrorProvider implements ArgumentsProvider {

    private static final CustomFaker customFaker = new CustomFaker();

    @Override
    @NonNull
    public Stream<? extends Arguments> provideArguments(
            @NonNull final ParameterDeclarations parameters,
            @NonNull final ExtensionContext context) {
        final var expectedDate = LocalDate.ofInstant(
                customFaker.timeAndDate().future(),
                UTC
        );
        return Stream.of(
                Arguments.of(null,
                        expectedDate,
                        customFaker.valueObject().recipient(),
                        customFaker.valueObject().address()
                ),
                Arguments.of(customFaker.valueObject().money(),
                        null,
                        customFaker.valueObject().recipient(),
                        customFaker.valueObject().address()
                ),
                Arguments.of(customFaker.valueObject().money(),
                        expectedDate,
                        null,
                        customFaker.valueObject().address()
                ),
                Arguments.of(customFaker.valueObject().money(),
                        expectedDate,
                        customFaker.valueObject().recipient(),
                        null
                )
        );
    }

}
