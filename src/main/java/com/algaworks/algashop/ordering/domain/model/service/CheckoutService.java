package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import org.jspecify.annotations.NonNull;

import java.util.Set;

import static java.util.Objects.requireNonNull;

@DomainService
public class CheckoutService {

    public Order checkout(final ShoppingCart shoppingCart,
                          final Billing billing,
                          final Shipping shipping,
                          final PaymentMethod paymentMethod) {
        requireNonNull(billing);
        requireNonNull(shipping);
        requireNonNull(paymentMethod);
        validateShoppingCart(shoppingCart);
        final var order = buildDraftOrder(shoppingCart, billing, shipping, paymentMethod);
        addItems(order, shoppingCart.items());
        placedOrder(order, shoppingCart);
        return order;
    }

    private static @NonNull Order buildDraftOrder(final ShoppingCart shoppingCart,
                                                  final Billing billing,
                                                  final Shipping shipping,
                                                  final PaymentMethod paymentMethod) {
        final var order = Order.draft(shoppingCart.customerId());
        order.changeShipping(shipping);
        order.changeBilling(billing);
        order.changePaymentMethod(paymentMethod);
        return order;
    }

    private static void validateShoppingCart(final ShoppingCart shoppingCart) {
        requireNonNull(shoppingCart);
        if (shoppingCart.isEmpty() || shoppingCart.containsUnavailable()){
            throw new ShoppingCartCantProceedToCheckoutException();
        }
    }

    private static void addItems(final Order order, final Set<ShoppingCartItem> items){
        items.forEach(i -> {
            final var product = new Product(i.productId(), i.name(), i.price(), i.isAvailable());
            order.addItem(product, i.quantity());
        });
    }

    private static void placedOrder(final Order order, final ShoppingCart shoppingCart){
        order.place();
        shoppingCart.empty();
    }

}
