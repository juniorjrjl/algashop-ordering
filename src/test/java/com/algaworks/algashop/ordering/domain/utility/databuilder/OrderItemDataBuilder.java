package com.algaworks.algashop.ordering.domain.utility.databuilder;

import com.algaworks.algashop.ordering.domain.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class OrderItemDataBuilder {

    private static final CustomFaker faker = new CustomFaker();

    @With
    private Supplier<OrderItemId> id = OrderItemId::new;
    @With
    private Supplier<OrderId> orderId = OrderId::new;
    @With
    private Supplier<Product> product = () -> ProductDataBuilder.builder().build();
    @With
    private Supplier<Quantity> quantity = () -> faker.valueObject().quantity(1, 10);

    public static OrderItemDataBuilder builder() {
        return new OrderItemDataBuilder();
    }

    public OrderItem buildNew() {
        return OrderItem.brandNew()
                .orderId(orderId.get())
                .product(product.get())
                .quantity(quantity.get())
                .build();
    }

    public OrderItem buildExisting() {
        return OrderItem.existing()
                .id(id.get())
                .orderId(orderId.get())
                .productId(product.get().id())
                .productName(product.get().name())
                .price(product.get().price())
                .quantity(quantity.get())
                .totalAmount(product.get().price())
                .build();
    }

    public Set<OrderItem> buildExistingList(final int amount){
        return Stream.generate(() -> OrderItemDataBuilder.builder().buildExisting())
                .limit(amount).collect(Collectors.toSet());
    }

}
