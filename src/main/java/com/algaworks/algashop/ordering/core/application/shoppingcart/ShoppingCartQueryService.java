package com.algaworks.algashop.ordering.core.application.shoppingcart;

import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ForQueryingShoppingCart;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartOutput;
import com.algaworks.algashop.ordering.core.port.out.shoppingcart.ForObtainingShoppingCart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartQueryService implements ForQueryingShoppingCart {

    private final ForObtainingShoppingCart forObtainingShoppingCart;

    @Override
    public ShoppingCartOutput findById(final UUID id) {
        return forObtainingShoppingCart.findById(id);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(final UUID customerId) {
        return forObtainingShoppingCart.findByCustomerId(customerId);
    }

}
