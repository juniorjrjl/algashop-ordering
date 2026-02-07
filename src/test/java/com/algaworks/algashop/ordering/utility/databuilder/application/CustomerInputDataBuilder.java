package com.algaworks.algashop.ordering.utility.databuilder.application;

import com.algaworks.algashop.ordering.application.common.AddressData;
import com.algaworks.algashop.ordering.application.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerInputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> firstName = () -> customFaker.name().firstName();
    @With
    private Supplier<String> lastName = () -> customFaker.name().lastName();
    @With
    private Supplier<String> email = () -> customFaker.internet().emailAddress();
    @With
    private Supplier<String> phone = () -> customFaker.phoneNumber().cellPhone();
    @With
    private Supplier<String> document = () -> customFaker.cpf().valid();
    @With
    private Supplier<LocalDate> birthDate = () -> customFaker.timeAndDate().birthday();
    @With
    private Supplier<Boolean> promotionNotificationsAllowed = () -> customFaker.bool().bool();
    @With
    private Supplier<AddressData> address = () -> customFaker.commonApplication().address();

    public static CustomerInputDataBuilder builder(){
        return new CustomerInputDataBuilder();
    }

    public CustomerInput build(){
        return CustomerInput.builder()
                .firstName(firstName.get())
                .lastName(lastName.get())
                .email(email.get())
                .phone(phone.get())
                .document(document.get())
                .birthDate(birthDate.get())
                .promotionNotificationsAllowed(promotionNotificationsAllowed.get())
                .address(address.get())
                .build();
    }

}
