package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@DomainService
@RequiredArgsConstructor
public class CheckoutService {

    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;

    public Order checkout(final Customer customer,
                          final ShoppingCart shoppingCart,
                          final Billing billing,
                          final Shipping shipping,
                          final PaymentMethod paymentMethod) {
        validateShoppingCart(shoppingCart);
        final var order = buildDraftOrder(customer, shoppingCart, billing, shipping, paymentMethod);
        addItems(order, shoppingCart.items());
        placedOrder(order, shoppingCart);
        return order;
    }

    private Order buildDraftOrder(final Customer customer,
                                  final ShoppingCart shoppingCart,
                                  final Billing billing,
                                  final Shipping shipping,
                                  final PaymentMethod paymentMethod) {
        final var order = Order.draft(shoppingCart.customerId());
        if (haveFreeShipping(customer)){
            final var freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }
        order.changeShipping(shipping);
        order.changeBilling(billing);
        order.changePaymentMethod(paymentMethod);
        return order;
    }

    private static void validateShoppingCart(final ShoppingCart shoppingCart) {
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

    private boolean haveFreeShipping(final Customer  customer) {
        return this.customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }

}
