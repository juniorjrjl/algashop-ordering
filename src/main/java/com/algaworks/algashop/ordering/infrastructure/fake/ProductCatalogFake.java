package com.algaworks.algashop.ordering.infrastructure.fake;

import com.algaworks.algashop.ordering.domain.model.service.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;

import java.util.Optional;

public class ProductCatalogFake implements ProductCatalogService {
    @Override
    public Optional<Product> ofId(final ProductId productId) {
        final var product = Product.builder()
                .id(productId)
                .name(new ProductName("Product Name"))
                .price(new Money("100.00"))
                .inStock(true)
                .build();
        return Optional.of(product);
    }
}
