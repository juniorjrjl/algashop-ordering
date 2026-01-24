package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;
import java.util.function.Consumer;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessage.ERROR_CUSTOMER_ARCHIVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@UnitTest
class CustomerTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void givenUnarchivedCustomerWhenArchiveShouldAnonymize(){
        var email = customFaker.valueObject().email();
        var address = customFaker.valueObject().address();
        var customer = CustomerDataBuilder.builder()
                .withEmail(() -> email)
                .withAddress(() -> address)
                .buildNew();
        customer.archive();
        assertWith(customer,
                c -> assertThat(c.fullName()).isEqualTo(FullName.ANONYMOUS),
                c -> assertThat(c.email()).isNotEqualTo(email),
                c -> assertThat(c.email()).isEqualTo(Email.ANONYMOUS),
                c -> assertThat(c.phone()).isEqualTo(Phone.ANONYMOUS),
                c -> assertThat(c.document()).isEqualTo(Document.ANONYMOUS),
                c -> assertThat(c.birthDate()).isNull(),
                c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
                c -> assertThat(c.address()).isEqualTo(address.toBuilder()
                        .number("Anonymous")
                        .complement(null)
                        .build())
        );
    }

    private static final List<Arguments> givenArchivedCustomerWhenTryToChangeIdShouldThrowException =
            List.of(
                    Arguments.of((Consumer<Customer>) Customer::archive),
                    Arguments.of((Consumer<Customer>) c -> c.changeEmail(customFaker.valueObject().email())),
                    Arguments.of((Consumer<Customer>) c -> c.changePhone(customFaker.valueObject().phone()))
            );

    @ParameterizedTest
    @FieldSource
    void givenArchivedCustomerWhenTryToChangeIdShouldThrowException(final Consumer<Customer> changeCustomer){
        var customer = CustomerDataBuilder.builder().buildNew();
        customer.archive();
        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> changeCustomer.accept(customer))
                .withMessage(ERROR_CUSTOMER_ARCHIVED);
    }

    @Test
    void givenBrandNewCustomerWhenAddValidLoyaltyPointsShouldSumPoints(){
        var customer = CustomerDataBuilder.builder().buildNew();
        var firstPoints = customFaker.valueObject().loyaltyPoints(1, 30);
        customer.addLoyaltyPoints(firstPoints);
        var secondPoints = customFaker.valueObject().loyaltyPoints(1, 30);
        customer.addLoyaltyPoints(secondPoints);

        assertThat(customer.loyaltyPoints()).isEqualTo(firstPoints.add(secondPoints));
    }

}