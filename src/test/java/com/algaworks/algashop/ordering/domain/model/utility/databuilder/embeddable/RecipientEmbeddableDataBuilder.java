package com.algaworks.algashop.ordering.domain.model.utility.databuilder.embeddable;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class RecipientEmbeddableDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> firstName = () -> customFaker.name().firstName();
    @With
    private Supplier<String> lastName = () -> customFaker.name().lastName();
    @With
    private Supplier<String> document = () -> customFaker.cpf().valid();
    @With
    private Supplier<String> phone = ()  -> customFaker.phoneNumber().cellPhone();

    public static RecipientEmbeddableDataBuilder create() {
        return new RecipientEmbeddableDataBuilder();
    }

    public RecipientEmbeddable build() {
        return new RecipientEmbeddable(
                firstName.get(),
                lastName.get(),
                document.get(),
                phone.get()
        );
    }

}
