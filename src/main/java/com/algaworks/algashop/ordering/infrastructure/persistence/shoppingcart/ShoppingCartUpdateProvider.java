package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartProductAdjustmentService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ShoppingCartUpdateProvider implements ShoppingCartProductAdjustmentService {

    private final ShoppingCartPersistenceEntityRepository repository;

    @Transactional
    @Override
    public void adjustPrice(final ProductId productId, final Money updatePrice) {
        repository.updateItemPrice(productId.value(), updatePrice.value());
        repository.recalculateTotalsForCartsWithProduct(productId.value());
    }

    @Transactional
    @Override
    public void changeAvailability(final ProductId productId, final boolean available) {
        repository.updateItemAvailability(productId.value(), available);
    }

}
