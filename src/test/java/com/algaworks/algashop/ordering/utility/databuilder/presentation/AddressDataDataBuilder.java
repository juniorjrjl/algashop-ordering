package com.algaworks.algashop.ordering.utility.databuilder.presentation;

import com.algaworks.algashop.ordering.core.port.in.common.AddressData;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class AddressDataDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> street = () -> customFaker.address().streetName();
    @With
    private Supplier<String> complement = () -> customFaker.address().buildingNumber();
    @With
    private Supplier<String> neighborhood = () -> customFaker.lorem().word();
    @With
    private Supplier<String> number = () -> customFaker.address().streetAddressNumber();
    @With
    private Supplier<String> city = () -> customFaker.address().city();
    @With
    private Supplier<String> state = () -> customFaker.address().state();
    @With
    private Supplier<String> zipCode = () -> customFaker.address().zipCode();

    public static AddressDataDataBuilder builder() {
        return new AddressDataDataBuilder();
    }

    public AddressData build(){
        return new AddressData(
                street.get(),
                complement.get(),
                neighborhood.get(),
                number.get(),
                city.get(),
                state.get(),
                zipCode.get()
        );
    }

}
