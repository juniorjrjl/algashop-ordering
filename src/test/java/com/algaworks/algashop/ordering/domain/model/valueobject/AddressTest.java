package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;

class AddressTest {

    private static final CustomFaker customFaker = new CustomFaker();

    private static final List<Arguments> shouldCreateAddress = List.of(
            Arguments.of(
                    customFaker.address().streetAddress(),
                    null,
                    customFaker.lorem().word(),
                    customFaker.address().streetAddressNumber(),
                    customFaker.address().city(),
                    customFaker.address().state(),
                    customFaker.valueObject().zipCode()
            ),
            Arguments.of(
                    customFaker.address().streetAddress(),
                    customFaker.address().buildingNumber(),
                    customFaker.lorem().word(),
                    customFaker.address().streetAddressNumber(),
                    customFaker.address().city(),
                    customFaker.address().state(),
                    customFaker.valueObject().zipCode()
            )
    );

    @ParameterizedTest
    @FieldSource
    void shouldCreateAddress(final String street,
                             final String complement,
                             final String neighborhood,
                             final String number,
                             final String city,
                             final String state,
                             final ZipCode zipCode) {
        var address = new Address(street, complement, neighborhood, number, city, state, zipCode);
        assertWith(address,
                a -> assertThat(a.street()).isEqualTo(street),
                a -> assertThat(a.complement()).isEqualTo(complement),
                a -> assertThat(a.neighborhood()).isEqualTo(neighborhood),
                a -> assertThat(a.number()).isEqualTo(number),
                a -> assertThat(a.city()).isEqualTo(city),
                a -> assertThat(a.state()).isEqualTo(state),
                a -> assertThat(a.zipCode()).isEqualTo(zipCode)
        );

    }

    @ParameterizedTest
    @ArgumentsSource(AddressTestErrorProvider.class)
    void shouldNotCreateAddress(final String street,
                                final String complement,
                                final String neighborhood,
                                final String number,
                                final String city,
                                final String state,
                                final ZipCode zipCode,
                                final Class<? extends Exception> expectedException) {
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> new Address(
                        street,
                        complement,
                        neighborhood,
                        number,
                        city,
                        state,
                        zipCode
                ));
    }

}
