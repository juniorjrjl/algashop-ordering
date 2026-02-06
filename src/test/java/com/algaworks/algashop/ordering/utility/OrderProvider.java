package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import net.datafaker.providers.base.AbstractProvider;

import java.time.LocalDate;

import static java.time.ZoneOffset.UTC;

public class OrderProvider extends AbstractProvider<CustomFaker> {

    protected OrderProvider(final CustomFaker faker) {
        super(faker);
    }

    public Billing billing(){
        return Billing.builder()
                .fullName(faker.common().fullName())
                .document(faker.common().document())
                .phone(faker.common().phone())
                .address(faker.common().address())
                .email(faker.common().email())
                .build();
    }

    public Recipient recipient(){
        return Recipient.builder()
                .fullName(faker.common().fullName())
                .document(faker.common().document())
                .phone(faker.common().phone())
                .build();
    }

    public Shipping shipping(){
        return Shipping.builder()
                .cost(faker.common().money(1, 200))
                .expectedDate(LocalDate.ofInstant(faker.timeAndDate().future(), UTC))
                .recipient(recipient())
                .address(faker.common().address())
                .build();
    }

}
