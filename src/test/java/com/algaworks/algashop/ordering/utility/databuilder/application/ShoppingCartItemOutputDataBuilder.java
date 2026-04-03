package com.algaworks.algashop.ordering.utility.databuilder.application;

import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ShoppingCartItemOutputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private Supplier<UUID> id = UUID::randomUUID;
    private Supplier<UUID> productId = UUID::randomUUID;
    private Supplier<String> name = () -> customFaker.name().firstName();
    private Supplier<BigDecimal> price = () -> customFaker.numeric().nonZeroPositive();
    private Supplier<Integer> quantity = () -> customFaker.number().randomDigitNotZero();
    private Supplier<BigDecimal> totalAmount = () -> customFaker.numeric().nonZeroPositive();
    private Supplier<Boolean> available = () -> customFaker.bool().bool();

    public static ShoppingCartItemOutputDataBuilder builder() {
        return new ShoppingCartItemOutputDataBuilder();
    }

    public ShoppingCartItemOutput build() {
        final var output = new ShoppingCartItemOutput();
        output.setId(id.get());
        output.setProductId(productId.get());
        output.setName(name.get());
        output.setPrice(price.get());
        output.setQuantity(quantity.get());
        output.setTotalAmount(totalAmount.get());
        output.setAvailable(available.get());
        return output;
    }

    public List<ShoppingCartItemOutput> build(final int amount) {
        return Stream.generate(this::build)
                .limit(amount).toList();
    }

}
