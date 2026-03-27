package com.algaworks.algashop.ordering.utility.databuilder.presentation;

import com.algaworks.algashop.ordering.application.order.query.OrderItemDataDetailOutput;
import com.algaworks.algashop.ordering.domain.model.IdGenerator;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class OrderItemDataDetailOutputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> id = () -> IdGenerator.generateTSID().toString();
    @With
    private Supplier<String> orderId = () -> IdGenerator.generateTSID().toString();
    @With
    private Supplier<Product> product = () -> ProductDataBuilder.builder()
            .withInStock(() -> true)
            .build();
    @With
    private Supplier<Integer> quantity = () -> customFaker.number().numberBetween(1, 10);

    public static OrderItemDataDetailOutputDataBuilder builder() {
        return new OrderItemDataDetailOutputDataBuilder();
    }

    public OrderItemDataDetailOutput build() {
        return OrderItemDataDetailOutput.builder()
                .id(id.get())
                .orderId(orderId.get())
                .productId(product.get().id().value())
                .productName(product.get().name().value())
                .price(product.get().price().value())
                .quantity(quantity.get())
                .totalAmount(product.get().price().value())
                .build();
    }

    public List<OrderItemDataDetailOutput> buildList(final int amount){
        return Stream.generate(this::build)
                .limit(amount).toList();
    }

}
