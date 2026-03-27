package com.algaworks.algashop.ordering.utility.databuilder.presentation;

import com.algaworks.algashop.ordering.application.common.AddressData;
import com.algaworks.algashop.ordering.application.order.query.RecipientData;
import com.algaworks.algashop.ordering.application.order.query.ShippingData;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.ShippingEmbeddable;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.embeddable.AddressEmbeddableDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.embeddable.RecipientEmbeddableDataBuilder;
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
public class ShippingDataDataBuilder {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<BigDecimal> cost =
            () -> new BigDecimal(Double.toString(customFaker.number().randomDouble(2 ,1, 100)));
    @With
    private Supplier<LocalDate> expectedDate =
            () -> LocalDate.ofInstant(customFaker.timeAndDate().future(), ZoneId.of("UTC"));
    @With
    private Supplier<AddressData> address = () -> AddressDataDataBuilder.builder().build();
    @With
    private Supplier<RecipientData> recipient = () -> RecipientDataDataBuilder.builder().build();

    public static ShippingDataDataBuilder builder() {
        return new ShippingDataDataBuilder();
    }

    public ShippingData build() {
        return  new ShippingData(
                cost.get(),
                expectedDate.get(),
                recipient.get(),
                address.get()
        );
    }

}
