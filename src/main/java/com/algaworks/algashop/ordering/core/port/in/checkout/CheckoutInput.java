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
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CheckoutInput {

    @NotNull
    private UUID shoppingCartId;
    @NotBlank
    private String paymentMethod;
    @Valid
    @NotNull
    private ShippingInput shipping;
    @Valid
    @NotNull
    private BillingData billing;

    @Nullable
    private UUID creditCardId;

}