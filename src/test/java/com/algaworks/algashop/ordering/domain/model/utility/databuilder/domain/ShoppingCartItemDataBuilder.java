package com.algaworks.algashop.ordering.domain.model.utility.databuilder.domain;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
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
public class ShoppingCartItemDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<ShoppingCartItemId> id = ShoppingCartItemId::new;
    @With
    private Supplier<ShoppingCartId> shoppingCartId =  ShoppingCartId::new;
    @With
    private Supplier<ProductId> productId =  ProductId::new;
    @With
    private Supplier<ProductName> name = () -> customFaker.valueObject().productName();
    @With
    private Supplier<Money> price = () ->  customFaker.valueObject().money();
    @With
    private Supplier<Quantity> quantity = () -> customFaker.valueObject().quantity(1, 10);
    @With
    private Supplier<Boolean> available = () -> customFaker.bool().bool();

    public static ShoppingCartItemDataBuilder builder() {
        return new ShoppingCartItemDataBuilder();
    }

    public ShoppingCartItem build(){
        return ShoppingCartItem.existing()
                .id(id.get())
                .shoppingCartId(shoppingCartId.get())
                .productId(productId.get())
                .name(name.get())
                .price(price.get())
                .quantity(quantity.get())
                .available(available.get())
                .build();
    }

    public Set<ShoppingCartItem> buildSet(final int amount) {
        return Stream.generate(this::build)
                .limit(amount)
                .collect(Collectors.toSet());
    }

}
