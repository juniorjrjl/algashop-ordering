package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

import java.time.LocalDate;

import static java.time.ZoneOffset.UTC;

public class ValueObjectProvider extends AbstractProvider<BaseProviders> {

    protected ValueObjectProvider(final BaseProviders faker) {
        super(faker);
    }

    public ProductName productName() {
        return new ProductName(faker.book().title());
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

    public Recipient recipient(){
        return Recipient.builder()
                .fullName(fullName())
                .document(document())
                .phone(phone())
                .build();
    }

    public Shipping shipping(){
        return Shipping.builder()
                .cost(money(1, 200))
                .expectedDate(LocalDate.ofInstant(faker.timeAndDate().future(), UTC))
                .recipient(recipient())
                .address(address())
                .build();
    }

}
