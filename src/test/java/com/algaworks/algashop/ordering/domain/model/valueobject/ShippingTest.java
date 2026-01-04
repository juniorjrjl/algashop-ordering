package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.tag.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;

@UnitTest
class ShippingTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldCreate(){
        final var cost = customFaker.valueObject().money(5, 100);
        final var expectedDate = LocalDate.ofInstant(
                customFaker.timeAndDate().future(),
                UTC
        );
        final var recipient = customFaker.valueObject().recipient();
        final var address = customFaker.valueObject().address();
        final var billingInfo = Shipping.builder()
                .cost(cost)
                .expectedDate(expectedDate)
                .recipient(recipient)
                .address(address)
                .build();
        assertWith(billingInfo,
                b -> assertThat(b.cost()).isEqualTo(cost),
                b -> assertThat(b.expectedDate()).isEqualTo(expectedDate),
                b -> assertThat(b.recipient()).isEqualTo(recipient),
                b -> assertThat(b.address()).isEqualTo(address)
                );
    }

    @ParameterizedTest
    @ArgumentsSource(ShippingInfoTestErrorProvider.class)
    void shouldNotCreate(final Money cost,
                         final LocalDate expectedDate,
                         final Recipient recipient,
                         final Address address){
        final var builder = Shipping.builder()
                .cost(cost)
                .expectedDate(expectedDate)
                .recipient(recipient)
                .address(address);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(builder::build);
    }

}