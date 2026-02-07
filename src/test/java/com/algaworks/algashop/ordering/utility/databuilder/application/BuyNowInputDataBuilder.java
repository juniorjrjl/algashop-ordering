package com.algaworks.algashop.ordering.utility.databuilder.application;

import com.algaworks.algashop.ordering.application.checkout.BillingData;
import com.algaworks.algashop.ordering.application.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.application.checkout.ShippingInput;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.UUID;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class BuyNowInputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<ShippingInput> shipping = () -> customFaker.commonApplication().shipping();
    @With
    private Supplier<BillingData> billing = () -> customFaker.commonApplication().billing();
    @With
    private Supplier<UUID> productId = UUID::randomUUID;
    @With
    private Supplier<UUID> customerId = UUID::randomUUID;
    @With
    private Supplier<Integer> quantity = () -> customFaker.number().positive();
    @With
    private Supplier<String> paymentMethod = () -> customFaker.options().option(PaymentMethod.class).name();

    public static BuyNowInputDataBuilder builder() {
        return new BuyNowInputDataBuilder();
    }

    public BuyNowInput build() {
        return BuyNowInput.builder()
                .shipping(shipping.get())
                .billing(billing.get())
                .productId(productId.get())
                .customerId(customerId.get())
                .quantity(quantity.get())
                .paymentMethod(paymentMethod.get())
                .build();
    }

}
