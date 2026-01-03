package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

public class AddressTestErrorProvider implements ArgumentsProvider {

    private static final CustomFaker customFaker = new CustomFaker();

    @Override
    @NonNull
    public Stream<? extends Arguments> provideArguments(
            @NonNull final ParameterDeclarations parameters,
            @NonNull final ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        null,
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        null,
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        null,
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        null,
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        null,
                        customFaker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        customFaker.address().state(),
                        null,
                        NullPointerException.class
                ),

                Arguments.of(
                        "",
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        "",
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        "",
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        "",
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        "",
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),

                Arguments.of(
                        " ",
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        " ",
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        " ",
                        customFaker.address().city(),
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        " ",
                        customFaker.address().state(),
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        customFaker.address().streetAddress(),
                        customFaker.address().buildingNumber(),
                        customFaker.lorem().word(),
                        customFaker.address().streetAddressNumber(),
                        customFaker.address().city(),
                        " ",
                        customFaker.valueObject().zipCode(),
                        IllegalArgumentException.class
                )
        );
    }
}
