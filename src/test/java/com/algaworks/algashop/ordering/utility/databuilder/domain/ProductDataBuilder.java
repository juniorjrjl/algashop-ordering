package com.algaworks.algashop.ordering.utility.databuilder.domain;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
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
public class ProductDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<ProductId> id = ProductId::new;
    @With
    private Supplier<ProductName> name = () -> customFaker.product().productName();
    @With
    private Supplier<Money> price = () -> customFaker.common().money(5, 999);
    @With
    private Supplier<Boolean> inStock = () -> customFaker.bool().bool();

    public static ProductDataBuilder builder() {
        return new ProductDataBuilder();
    }

    public Product build(){
        return Product.builder()
                .id(id.get())
                .name(name.get())
                .price(price.get())
                .inStock(inStock.get())
                .build();
    }

    public Set<Product> buildList(final int amount){
        return Stream.generate(this::build)
                .limit(amount).collect(Collectors.toSet());
    }

}
