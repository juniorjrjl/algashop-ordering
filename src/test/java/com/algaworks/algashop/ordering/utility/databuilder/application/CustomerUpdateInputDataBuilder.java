package com.algaworks.algashop.ordering.utility.databuilder.application;

import com.algaworks.algashop.ordering.application.common.AddressData;
import com.algaworks.algashop.ordering.application.customer.management.CustomerUpdateInput;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerUpdateInputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> firstName = () -> customFaker.name().firstName();
    @With
    private Supplier<String> lastName = () -> customFaker.name().lastName();
    @With
    private Supplier<String> phone = () -> customFaker.phoneNumber().cellPhone();
    @With
    private Supplier<Boolean> promotionNotificationsAllowed = () -> customFaker.bool().bool();
    @With
    private Supplier<AddressData> address = () -> customFaker.commonApplication().address();

    public static CustomerUpdateInputDataBuilder builder() {
        return new CustomerUpdateInputDataBuilder();
    }

    public CustomerUpdateInput build(){
        return CustomerUpdateInput.builder()
                .firstName(firstName.get())
                .lastName(lastName.get())
                .phone(phone.get())
                .promotionNotificationsAllowed(promotionNotificationsAllowed.get())
                .address(address.get())
                .build();
    }

}
