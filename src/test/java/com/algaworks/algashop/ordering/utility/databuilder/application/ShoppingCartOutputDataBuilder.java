package com.algaworks.algashop.ordering.utility.databuilder.application;

import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartItemOutput;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartOutput;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ShoppingCartOutputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<UUID> id = UUID::randomUUID;
    @With
    private Supplier<UUID> customerId = UUID::randomUUID;
    @With
    private Supplier<Integer> totalItems = () -> customFaker.number().numberBetween(1, 10);
    @With
    private Supplier<BigDecimal> totalAmount = () -> customFaker.numeric().nonZeroPositive();
    @With
    private Supplier<List<ShoppingCartItemOutput>> items = () -> ShoppingCartItemOutputDataBuilder.builder()
            .build(customFaker.number().randomDigitNotZero());

    public static ShoppingCartOutputDataBuilder builder() {
        return new ShoppingCartOutputDataBuilder();
    }

    public ShoppingCartOutput build() {
        final var output = new ShoppingCartOutput();
        output.setId(id.get());
        output.setCustomerId(customerId.get());
        output.setTotalItems(totalItems.get());
        output.setTotalAmount(totalAmount.get());
        output.setItems(items.get());
        return output;
    }

    public List<ShoppingCartOutput> build(final int amount) {
        return Stream.generate(this::build)
                .limit(amount).toList();
    }

}
