package com.algaworks.algashop.ordering.domain.utility.databuilder;

import com.algaworks.algashop.ordering.domain.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ProductDataBuilder {

    private static final CustomFaker customFaker = new CustomFaker();

    @With
    private Supplier<ProductId> id = ProductId::new;
    @With
    private Supplier<ProductName> name = () -> customFaker.valueObject().productName();
    @With
    private Supplier<Money> price = () -> customFaker.valueObject().money(5, 999);
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

}
