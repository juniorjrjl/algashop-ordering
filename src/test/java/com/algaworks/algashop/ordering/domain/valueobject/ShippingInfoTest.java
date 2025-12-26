package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;

class ShippingInfoTest {

    private static final CustomFaker customFaker = new CustomFaker();

    @Test
    void shouldCreate(){
        final var fullName = customFaker.valueObject().fullName();
        final var document = customFaker.valueObject().document();
        final var phone = customFaker.valueObject().phone();
        final var address = customFaker.valueObject().address();
        final var billingInfo = ShippingInfo.builder()
                .fullName(fullName)
                .document(document)
                .phone(phone)
                .address(address)
                .build();
        assertWith(billingInfo,
                b -> assertThat(b.fullName()).isEqualTo(fullName),
                b -> assertThat(b.document()).isEqualTo(document),
                b -> assertThat(b.phone()).isEqualTo(phone),
                b -> assertThat(b.address()).isEqualTo(address)
                );
    }

    @ParameterizedTest
    @ArgumentsSource(ShippingInfoTestErrorProvider.class)
    void shouldNotCreate(final FullName fullName,
                         final Document document,
                         final Phone phone,
                         final Address address){
        final var builder = ShippingInfo.builder()
                .fullName(fullName)
                .document(document)
                .phone(phone)
                .address(address);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(builder::build);
    }

}