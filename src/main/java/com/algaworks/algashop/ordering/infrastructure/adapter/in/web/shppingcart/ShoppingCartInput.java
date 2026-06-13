package com.algaworks.algashop.ordering.infrastructure.adapter.in.web.shppingcart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ShoppingCartInput {
    @NotNull
    private UUID customerId;
}