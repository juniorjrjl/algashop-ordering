package com.algaworks.algashop.ordering.domain.model.utility.databuilder.embeddable;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class AddressEmbeddableDataBuilder {

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

    public static AddressEmbeddableDataBuilder builder() {
        return new AddressEmbeddableDataBuilder();
    }

    public AddressEmbeddable build(){
        return new AddressEmbeddable(
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
