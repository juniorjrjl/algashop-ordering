package com.algaworks.algashop.ordering.domain.utility;

import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.Email;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.valueobject.ZipCode;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class ValueObjectProvider extends AbstractProvider<BaseProviders> {

    protected ValueObjectProvider(final BaseProviders faker) {
        super(faker);
    }

    public CustomerId customerId() {
        return new CustomerId();
    }

    public FullName fullName() {
        return new FullName(faker.name().firstName(), faker.name().lastName());
    }

    public LoyaltyPoints loyaltyPoints() {
        return loyaltyPoints(1,100);
    }

    public LoyaltyPoints loyaltyPoints(final int min, final int max) {
        return new LoyaltyPoints(faker.number().numberBetween(min, max));
    }

    public BirthDate birthDate(){
        return new BirthDate(faker.timeAndDate().birthday());
    }

    public BirthDate birthDate(int minAge, int maxAge) {
        return new BirthDate(faker.timeAndDate().birthday(minAge, maxAge));
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
        return new Address(
                faker.address().streetAddress(),
                null,
                faker.lorem().word(),
                faker.address().streetAddressNumber(),
                faker.address().city(),
                faker.address().state(),
                zipCode()
                );
    }

    public Address addressWithComplement(){
        return new Address(
                faker.address().streetAddress(),
                faker.address().buildingNumber(),
                faker.lorem().characters(),
                faker.address().streetAddressNumber(),
                faker.address().city(),
                faker.address().state(),
                zipCode()
        );
    }

}
