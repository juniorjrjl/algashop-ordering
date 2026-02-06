package com.algaworks.algashop.ordering.domain.model.order.shipping;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;

public class ShippingInfoTestErrorProvider implements ArgumentsProvider {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

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
                        customFaker.order().recipient(),
                        customFaker.common().address()
                ),
                Arguments.of(customFaker.common().money(),
                        null,
                        customFaker.order().recipient(),
                        customFaker.common().address()
                ),
                Arguments.of(customFaker.common().money(),
                        expectedDate,
                        null,
                        customFaker.common().address()
                ),
                Arguments.of(customFaker.common().money(),
                        expectedDate,
                        customFaker.order().recipient(),
                        null
                )
        );
    }

}
