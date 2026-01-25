package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

@DomainService
@RequiredArgsConstructor
public class ShoppingService {

    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    public @NonNull ShoppingCart startShopping(@NonNull final CustomerId customerId){
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
