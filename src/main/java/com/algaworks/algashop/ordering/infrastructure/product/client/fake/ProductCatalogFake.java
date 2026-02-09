package com.algaworks.algashop.ordering.infrastructure.product.client.fake;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
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
