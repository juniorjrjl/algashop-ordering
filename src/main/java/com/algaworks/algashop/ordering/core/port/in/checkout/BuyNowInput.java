package com.algaworks.algashop.ordering.core.port.in.checkout;

import com.algaworks.algashop.ordering.core.port.in.order.ShippingInput;
import com.algaworks.algashop.ordering.core.port.in.order.BillingData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyNowInput {

    @NotNull
    private UUID productId;
    @NotNull
    private UUID customerId;
    @NotNull
    private Integer quantity;
    @NotBlank
    private String paymentMethod;
    @Nullable
    private UUID creditCardId;
    @NotNull
    @Valid
    private ShippingInput shipping;

    @NotNull
    @Valid
    private BillingData billing;

}
