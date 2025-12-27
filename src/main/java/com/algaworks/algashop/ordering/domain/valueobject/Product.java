package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.exception.OutOfStockException;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record Product(
        ProductId id,
        ProductName name,
        Money price,
        Boolean inStock
) {

    public Product{
        requireNonNull(id);
        requireNonNull(name);
        requireNonNull(price);
        requireNonNull(inStock);
    }

    private boolean isOutOfStock() {
        return !inStock;
    }

    public void checkOutOfStock() {
        if (isOutOfStock()) {
            throw new OutOfStockException(this.id());
        }
    }

}
