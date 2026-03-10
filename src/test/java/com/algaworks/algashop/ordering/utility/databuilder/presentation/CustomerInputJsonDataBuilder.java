package com.algaworks.algashop.ordering.utility.databuilder.presentation;

import com.algaworks.algashop.ordering.application.common.AddressData;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.JsonDataBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class CustomerInputJsonDataBuilder implements JsonDataBuilder {

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

    public static CustomerInputJsonDataBuilder builder(){
        return new CustomerInputJsonDataBuilder();
    }

    public ObjectNode build(){
        final var json = JsonNodeFactory.instance.objectNode();

        putNullable(json,"firstName", firstName.get());
        putNullable(json,"lastName", lastName.get());
        putNullable(json,"email", email.get());
        putNullable(json,"phone", phone.get());
        putNullable(json,"document", document.get());
        putNullable(json,"birthDate", customFaker.json().toJsonDate(birthDate.get()));
        putNullable(json,"promotionNotificationsAllowed", promotionNotificationsAllowed.get());

        final var addressValue = address.get();
        if (isNull(addressValue)){
            json.putNull("address");
        } else {
            final var addressJson = json.putObject("address");
            putNullable(addressJson, "street", addressValue.getStreet());
            putNullable(addressJson, "city", addressValue.getCity());
            putNullable(addressJson, "state", addressValue.getState());
            putNullable(addressJson, "zipCode", addressValue.getZipCode());
            putNullable(addressJson, "number", addressValue.getNumber());
            putNullable(addressJson, "complement", addressValue.getComplement());
            putNullable(addressJson, "neighborhood", addressValue.getNeighborhood());
        }

        return json;
    }

}
