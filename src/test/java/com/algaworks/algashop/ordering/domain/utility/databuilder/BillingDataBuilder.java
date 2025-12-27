package com.algaworks.algashop.ordering.domain.utility.databuilder;

import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.Email;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class BillingDataBuilder {

    private static final CustomFaker faker = new CustomFaker();

    @With
    private Supplier<FullName> fullName = () -> faker.valueObject().fullName();
    @With
    private Supplier<Document> document = () -> faker.valueObject().document();
    @With
    private Supplier<Phone> phone = () -> faker.valueObject().phone();
    @With
    private Supplier<Address> address = () -> faker.valueObject().addressWithComplement();
    @With
    private Supplier<Email> email = () -> faker.valueObject().email();

    public static BillingDataBuilder builder() {
        return new BillingDataBuilder();
    }

    public Billing build(){
        return Billing.builder()
                .fullName(fullName.get())
                .document(document.get())
                .phone(phone.get())
                .address(address.get())
                .email(email.get())
                .build();
    }

}
