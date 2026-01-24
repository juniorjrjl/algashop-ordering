package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;

public interface ShoppingCartProductAdjustmentService {

    void adjustPrice(final ProductId productId, final Money updatePrice);

    void changeAvailability(final ProductId productId, final boolean available);

}
