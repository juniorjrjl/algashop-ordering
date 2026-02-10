package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@DomainService
@RequiredArgsConstructor
public class ShoppingService {

    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    public ShoppingCart startShopping(final CustomerId customerId){
        requireNonNull(customerId);
        if (!customers.exists(customerId)){
            throw new CustomerNotFoundException();
        }
        if (shoppingCarts.ofCustomer(customerId).isPresent()){
            throw new CustomerAlreadyHaveShoppingCartException();
        }
        return ShoppingCart.startShopping(customerId);
    }

}
