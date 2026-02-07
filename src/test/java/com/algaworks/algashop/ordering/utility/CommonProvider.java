package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class CommonProvider extends AbstractProvider<CustomFaker> {

    protected CommonProvider(final CustomFaker faker) {
        super(faker);
    }

    public FullName fullName() {
        return new FullName(faker.name().firstName(), faker.name().lastName());
    }

    public Document document(){
        return new Document(faker.cpf().valid());
    }

    public Email email(){
        return new Email(faker.internet().safeEmailAddress());
    }

    public Email email(final String name) {
        return new Email(faker.internet().safeEmailAddress(name));
    }

    public Phone phone(){
        return new Phone(faker.phoneNumber().cellPhone());
    }

    public ZipCode zipCode(){
        return new ZipCode(faker.address().zipCode());
    }

    public Address address(){
        return Address.builder()
                .street(faker.address().streetAddress())
                .neighborhood(faker.lorem().characters())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(zipCode())
                .build();
    }

    public Address addressWithComplement(){
        return Address.builder()
                .street(faker.address().streetAddress())
                .complement(faker.address().buildingNumber())
                .neighborhood(faker.lorem().characters())
                .number(faker.address().streetAddressNumber())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(zipCode())
                .build();
    }

    public Money money(){
        return money(1, Integer.MAX_VALUE);
    }

    public Money money(final int min, final int max) {
        return new Money(Double.toString(faker.number().randomDouble(2 ,min, max)));
    }

    public Quantity quantity(){
        return quantity(0, Integer.MAX_VALUE);
    }

    public Quantity quantity(final int min, final int max) {
        return new Quantity(faker.number().numberBetween(min, max));
    }

}
