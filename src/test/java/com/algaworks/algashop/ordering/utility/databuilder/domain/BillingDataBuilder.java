package com.algaworks.algashop.ordering.utility.databuilder.domain;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class BillingDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<FullName> fullName = () -> customFaker.valueObject().fullName();
    @With
    private Supplier<Document> document = () -> customFaker.valueObject().document();
    @With
    private Supplier<Phone> phone = () -> customFaker.valueObject().phone();
    @With
    private Supplier<Address> address = () -> customFaker.valueObject().addressWithComplement();
    @With
    private Supplier<Email> email = () -> customFaker.valueObject().email();

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
