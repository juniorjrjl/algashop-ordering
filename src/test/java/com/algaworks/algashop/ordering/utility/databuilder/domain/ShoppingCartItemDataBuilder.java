package com.algaworks.algashop.ordering.utility.databuilder.domain;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
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
    private Supplier<ProductName> name = () -> customFaker.product().productName();
    @With
    private Supplier<Money> price = () ->  customFaker.common().money(1, 100);
    @With
    private Supplier<Quantity> quantity = () -> customFaker.common().quantity(1, 10);
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
