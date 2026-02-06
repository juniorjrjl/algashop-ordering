package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import org.jspecify.annotations.NonNull;

@DomainService
public class BuyNowService {

    public Order buyNow(@NonNull final Product product,
                        @NonNull final CustomerId customerId,
                        @NonNull final Billing billing,
                        @NonNull final Shipping shipping,
                        @NonNull final Quantity quantity,
                        @NonNull final PaymentMethod paymentMethod){
        product.checkOutOfStock();
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

}
