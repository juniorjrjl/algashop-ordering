package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.jspecify.annotations.NonNull;

import static java.util.Objects.requireNonNull;

@DomainService
public class BuyNowService {

    public Order buyNow(@NonNull final Product product,
                        @NonNull final CustomerId customerId,
                        @NonNull final Billing billing,
                        @NonNull final Shipping shipping,
                        @NonNull final Quantity quantity,
                        @NonNull final PaymentMethod paymentMethod){
        checkArguments(product, customerId, billing, shipping, quantity, paymentMethod);
        return buildOrder(product, customerId, billing, shipping, quantity, paymentMethod);
    }

    private static @NonNull Order buildOrder(final Product product,
                                             final CustomerId customerId,
                                             final Billing billing,
                                             final Shipping shipping,
                                             final Quantity quantity,
                                             final PaymentMethod paymentMethod) {
        final var order = Order.draft(customerId);
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, quantity);
        order.place();
        return order;
    }

    private static void checkArguments(final Product product,
                                       final CustomerId customerId,
                                       final Billing billing,
                                       final Shipping shipping,
                                       final Quantity quantity,
                                       final PaymentMethod paymentMethod) {
        requireNonNull(product);
        requireNonNull(customerId);
        requireNonNull(billing);
        requireNonNull(shipping);
        requireNonNull(quantity);
        requireNonNull(paymentMethod);
        if (quantity.equals(Quantity.ZERO)) {
            throw new IllegalArgumentException();
        }
        product.checkOutOfStock();
    }

}
