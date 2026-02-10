package com.algaworks.algashop.ordering.application.shoppingcart.management;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFound;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ShoppingCartManagementApplicationService {

    private final ShoppingCarts shoppingCarts;
    private final Customers customers;
    private final ShoppingService service;
    private final ProductCatalogService productCatalogService;

    @Transactional
    public UUID createNew(final UUID rawCustomerId){
        final var customerId = new CustomerId(rawCustomerId);
        if (!customers.exists(customerId)){
            throw new CustomerNotFoundException();
        }
        final var shoppingCart = service.startShopping(customerId);
        shoppingCarts.add(shoppingCart);
        return shoppingCart.id().value();
    }

    @Transactional
    public void addItem(final ShoppingCartItemInput input){
        final var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(input.getShoppingCartId()))
                .orElseThrow(ShoppingCartNotFound::new);
        final var product = productCatalogService.ofId(new ProductId(input.getProductId()))
                .orElseThrow(ProductNotFoundException::new);
        product.checkOutOfStock();
        shoppingCart.addItem(product, new Quantity(input.getQuantity()));
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public void removeItem(final UUID rawId, final UUID rawShoppingCartItemId){
        final var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(rawId))
                .orElseThrow(ShoppingCartNotFound::new);
        shoppingCart.removeItem(new ShoppingCartItemId(rawShoppingCartItemId));
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public void empty(final UUID rawId){
        final var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(rawId))
                .orElseThrow(ShoppingCartNotFound::new);
        shoppingCart.empty();
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    public void delete(final UUID rawId){
        final var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(rawId))
                .orElseThrow(ShoppingCartNotFound::new);
        shoppingCarts.remove(shoppingCart);
    }

}
