package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;

@UnitTest
class BillingTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldCreate(){
        final var fullName = customFaker.common().fullName();
        final var document = customFaker.common().document();
        final var phone = customFaker.common().phone();
        final var address = customFaker.common().address();
        final var email = customFaker.common().email();
        final var billingInfo = Billing.builder()
                .fullName(fullName)
                .document(document)
                .phone(phone)
                .address(address)
                .email(email)
                .build();
        assertWith(billingInfo,
                b -> assertThat(b.fullName()).isEqualTo(fullName),
                b -> assertThat(b.document()).isEqualTo(document),
                b -> assertThat(b.phone()).isEqualTo(phone),
                b -> assertThat(b.address()).isEqualTo(address)
                );
    }

    @ParameterizedTest
    @ArgumentsSource(BillingInfoTestErrorProvider.class)
    void shouldNotCreate(final FullName fullName,
                         final Document document,
                         final Phone phone,
                         final Address address){
        final var builder = Billing.builder()
                .fullName(fullName)
                .document(document)
                .phone(phone)
                .address(address);
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(builder::build);
    }

}