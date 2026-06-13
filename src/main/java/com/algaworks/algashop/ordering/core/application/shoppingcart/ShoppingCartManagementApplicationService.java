package com.algaworks.algashop.ordering.core.application.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingService;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ForManagingShoppingCart;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartItemInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ShoppingCartManagementApplicationService implements ForManagingShoppingCart {

    private final ShoppingCarts shoppingCarts;
    private final Customers customers;
    private final ShoppingService service;
    private final ProductCatalogService productCatalogService;

    @Transactional
    @Override
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
    @Override
    public void addItem(final ShoppingCartItemInput input){
        final var shoppingCartId = new ShoppingCartId(input.getShoppingCartId());
        final var shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        final var productId = new ProductId(input.getProductId());
        final var product = productCatalogService.ofId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.checkOutOfStock();
        shoppingCart.addItem(product, new Quantity(input.getQuantity()));
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public void removeItem(final UUID rawId, final UUID rawShoppingCartItemId){
        final var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(rawId))
                .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCart.removeItem(new ShoppingCartItemId(rawShoppingCartItemId));
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public void empty(final UUID rawId){
        final var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(rawId))
                .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCart.empty();
        shoppingCarts.add(shoppingCart);
    }

    @Transactional
    @Override
    public void delete(final UUID rawId){
        final var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(rawId))
                .orElseThrow(ShoppingCartNotFoundException::new);
        shoppingCarts.remove(shoppingCart);
    }

}
