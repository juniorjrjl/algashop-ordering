package com.algaworks.algashop.ordering.core.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;

public interface ShoppingCartProductAdjustmentService {

    void adjustPrice(final ProductId productId, final Money updatePrice);

    void changeAvailability(final ProductId productId, final boolean available);

}
