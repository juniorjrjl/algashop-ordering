package com.algaworks.algashop.ordering.infrastructure.adapter.out.web.product.client.fake;

import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.product.Product;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductName;

import java.util.Optional;

//@Component
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
