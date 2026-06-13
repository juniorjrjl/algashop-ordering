package com.algaworks.algashop.ordering.utility.databuilder.presentation;

import com.algaworks.algashop.ordering.core.port.in.order.RecipientData;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class RecipientDataDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> firstName = () -> customFaker.name().firstName();
    @With
    private Supplier<String> lastName = () -> customFaker.name().lastName();
    @With
    private Supplier<String> document = () -> customFaker.cpf().valid();
    @With
    private Supplier<String> phone = () -> customFaker.phoneNumber().cellPhone();

    public static RecipientDataDataBuilder builder(){
        return new RecipientDataDataBuilder();
    }

    public RecipientData build(){
        return new RecipientData(
                firstName.get(),
                lastName.get(),
                document.get(),
                phone.get()
        );
    }

}
