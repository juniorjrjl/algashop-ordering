package com.algaworks.algashop.ordering.domain.model.valueobject;

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
                        customFaker.valueObject().document(),
                        customFaker.valueObject().phone(),
                        customFaker.valueObject().address(),
                        customFaker.valueObject().email()
                ),
                Arguments.of(customFaker.valueObject().fullName(),
                        null,
                        customFaker.valueObject().phone(),
                        customFaker.valueObject().address(),
                        customFaker.valueObject().email()
                ),
                Arguments.of(customFaker.valueObject().fullName(),
                        customFaker.valueObject().document(),
                        null,
                        customFaker.valueObject().address(),
                        customFaker.valueObject().email()
                ),
                Arguments.of(customFaker.valueObject().fullName(),
                        customFaker.valueObject().document(),
                        customFaker.valueObject().phone(),
                        null,
                        customFaker.valueObject().email()
                ),
                Arguments.of(customFaker.valueObject().fullName(),
                        customFaker.valueObject().document(),
                        customFaker.valueObject().phone(),
                        customFaker.valueObject().address(),
                        null)
        );
    }

}
