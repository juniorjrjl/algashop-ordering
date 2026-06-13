package com.algaworks.algashop.ordering.utility.databuilder.presentation;

import com.algaworks.algashop.ordering.core.port.in.common.AddressData;
import com.algaworks.algashop.ordering.core.port.in.order.BillingData;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class BillingDataDataBuilder {

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
    private Supplier<AddressData> address = () -> AddressDataDataBuilder.builder().build();

    public static BillingDataDataBuilder builder(){
        return new BillingDataDataBuilder();
    }

    public BillingData build(){
        return new BillingData(
                firstName.get(),
                lastName.get(),
                document.get(),
                phone.get(),
                email.get(),
                address.get()
        );
    }

}
