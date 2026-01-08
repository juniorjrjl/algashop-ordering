package com.algaworks.algashop.ordering.domain.model.utility.databuilder.embeddable;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ShippingEmbeddableDataBuilder {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<BigDecimal> cost =
            () -> new BigDecimal(Double.toString(customFaker.number().randomDouble(2 ,1, 100)));
    @With
    private Supplier<LocalDate> expectedDate =
            () -> LocalDate.ofInstant(customFaker.timeAndDate().future(), ZoneId.of("UTC"));
    @With
    private Supplier<AddressEmbeddable> address = () -> AddressEmbeddableDataBuilder.builder().build();
    @With
    private Supplier<RecipientEmbeddable> recipient = () -> RecipientEmbeddableDataBuilder.create().build();

    public static ShippingEmbeddableDataBuilder builder() {
        return new ShippingEmbeddableDataBuilder();
    }

    public ShippingEmbeddable build() {
        return  new ShippingEmbeddable(
                cost.get(),
                expectedDate.get(),
                address.get(),
                recipient.get()
        );
    }

}
