package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

public class AddressTestErrorProvider implements ArgumentsProvider {

    private static final CustomFaker faker = new CustomFaker();

    @Override
    @NonNull
    public Stream<? extends Arguments> provideArguments(
            @NonNull final ParameterDeclarations parameters,
            @NonNull final ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        null,
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        null,
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        null,
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        null,
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        null,
                        faker.valueObject().zipCode(),
                        NullPointerException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        faker.address().state(),
                        null,
                        NullPointerException.class
                ),

                Arguments.of(
                        "",
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        "",
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        "",
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        "",
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        "",
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),

                Arguments.of(
                        " ",
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        " ",
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        " ",
                        faker.address().city(),
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        " ",
                        faker.address().state(),
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                ),
                Arguments.of(
                        faker.address().streetAddress(),
                        faker.address().buildingNumber(),
                        faker.lorem().word(),
                        faker.address().streetAddressNumber(),
                        faker.address().city(),
                        " ",
                        faker.valueObject().zipCode(),
                        IllegalArgumentException.class
                )
        );
    }
}
