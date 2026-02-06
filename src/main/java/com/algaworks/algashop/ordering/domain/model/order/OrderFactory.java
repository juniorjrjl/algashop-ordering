package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import lombok.NoArgsConstructor;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class OrderFactory {

    public static Order filled(final CustomerId customerId,
                               final Shipping shipping,
                               final Billing billing,
                               final PaymentMethod paymentMethod,
                               final Product product,
                               final Quantity quantity) {
        requireNonNull(customerId);
        requireNonNull(shipping);
        requireNonNull(billing);
        requireNonNull(paymentMethod);
        requireNonNull(product);
        requireNonNull(quantity);

        final var order = Order.draft(customerId);
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        order.addItem(product, quantity);
        return order;
    }

}
