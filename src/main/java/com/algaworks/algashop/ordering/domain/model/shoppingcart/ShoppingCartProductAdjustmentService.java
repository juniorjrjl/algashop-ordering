package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;

public interface ShoppingCartProductAdjustmentService {

    void adjustPrice(final ProductId productId, final Money updatePrice);

    void changeAvailability(final ProductId productId, final boolean available);

}
