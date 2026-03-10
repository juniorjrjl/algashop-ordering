package com.algaworks.algashop.ordering.utility.databuilder.application;

import com.algaworks.algashop.ordering.application.common.AddressData;
import com.algaworks.algashop.ordering.application.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerOutputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<UUID> id = UUID::randomUUID;
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
    private Supplier<Integer> loyaltyPoints = () -> customFaker.number().numberBetween(10, 20);
    @With
    private Supplier<OffsetDateTime> registeredAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> archivedAt = OffsetDateTime::now;
    @With
    private Supplier<Boolean> archived = () -> customFaker.bool().bool();
    @With
    private Supplier<AddressData> address = () -> customFaker.commonApplication().address();

    public static CustomerOutputDataBuilder builder(){
        return new CustomerOutputDataBuilder();
    }

    public CustomerOutput build(){
        return CustomerOutput.builder()
                .id(id.get())
                .firstName(firstName.get())
                .lastName(lastName.get())
                .email(email.get())
                .phone(phone.get())
                .document(document.get())
                .birthDate(birthDate.get())
                .promotionNotificationsAllowed(promotionNotificationsAllowed.get())
                .loyaltyPoints(loyaltyPoints.get())
                .registeredAt(registeredAt.get())
                .archived(archived.get())
                .address(address.get())
                .build();
    }

}
