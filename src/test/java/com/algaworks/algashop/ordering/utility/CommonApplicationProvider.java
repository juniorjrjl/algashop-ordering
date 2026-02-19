package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.application.order.query.BillingData;
import com.algaworks.algashop.ordering.application.checkout.CheckoutInput;
import com.algaworks.algashop.ordering.application.order.query.RecipientData;
import com.algaworks.algashop.ordering.application.checkout.ShippingInput;
import com.algaworks.algashop.ordering.application.common.AddressData;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import net.datafaker.providers.base.AbstractProvider;

import java.util.UUID;

public class CommonApplicationProvider extends AbstractProvider<CustomFaker> {

    protected CommonApplicationProvider(final CustomFaker faker) {
        super(faker);
    }


    public AddressData address(){
        return AddressData.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .neighborhood(faker.lorem().characters())
                .build();
    }

    public AddressData addressWithComplement(){
        return AddressData.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(faker.address().zipCode())
                .neighborhood(faker.lorem().characters())
                .complement(faker.address().buildingNumber())
                .build();
    }

    public BillingData billing(){
        return BillingData.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .document(faker.cpf().valid())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .address(addressWithComplement())
                .build();
    }

    public ShippingInput shipping(){
        return ShippingInput.builder()
                .recipient(recipient())
                .address(addressWithComplement())
                .build();
    }

    public RecipientData recipient(){
        return RecipientData.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .document(faker.cpf().valid())
                .phone(faker.phoneNumber().phoneNumber())
                .build();
    }

    public CheckoutInput checkout(){
        return CheckoutInput.builder()
                .shoppingCartId(UUID.randomUUID())
                .paymentMethod(faker.options().option(PaymentMethod.class).name())
                .shipping(shipping())
                .billing(billing())
                .build();
    }

}
