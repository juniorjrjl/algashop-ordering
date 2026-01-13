package com.algaworks.algashop.ordering.utility.databuilder.embeddable;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class BillingEmbeddableDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> firstName = () -> customFaker.name().firstName();
    @With
    private Supplier<String> lastName = () -> customFaker.name().lastName();
    @With
    private Supplier<String> document = () -> customFaker.cpf().valid();
    @With
    private Supplier<String> phone = () -> customFaker.phoneNumber().cellPhone();
    @With
    private Supplier<String> email = () -> customFaker.internet().emailAddress();
    @With
    private Supplier<AddressEmbeddable> address = () -> AddressEmbeddableDataBuilder.builder().build();

    public static BillingEmbeddableDataBuilder builder(){
        return new BillingEmbeddableDataBuilder();
    }

    public BillingEmbeddable build(){
        return new BillingEmbeddable(
                firstName.get(),
                lastName.get(),
                document.get(),
                phone.get(),
                email.get(),
                address.get()
        );
    }

}
