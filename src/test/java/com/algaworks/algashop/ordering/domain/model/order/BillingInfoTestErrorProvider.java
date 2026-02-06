package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

public class BillingInfoTestErrorProvider implements ArgumentsProvider {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Override
    @NonNull
    public Stream<? extends Arguments> provideArguments(
            @NonNull final ParameterDeclarations parameters,
            @NonNull final ExtensionContext context) {
        return Stream.of(
                Arguments.of(null,
                        customFaker.common().document(),
                        customFaker.common().phone(),
                        customFaker.common().address(),
                        customFaker.common().email()
                ),
                Arguments.of(customFaker.common().fullName(),
                        null,
                        customFaker.common().phone(),
                        customFaker.common().address(),
                        customFaker.common().email()
                ),
                Arguments.of(customFaker.common().fullName(),
                        customFaker.common().document(),
                        null,
                        customFaker.common().address(),
                        customFaker.common().email()
                ),
                Arguments.of(customFaker.common().fullName(),
                        customFaker.common().document(),
                        customFaker.common().phone(),
                        null,
                        customFaker.common().email()
                ),
                Arguments.of(customFaker.common().fullName(),
                        customFaker.common().document(),
                        customFaker.common().phone(),
                        customFaker.common().address(),
                        null)
        );
    }

}
