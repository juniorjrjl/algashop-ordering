package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.service.ShoppingCartProductAdjustmentService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceEntityRepository;
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
