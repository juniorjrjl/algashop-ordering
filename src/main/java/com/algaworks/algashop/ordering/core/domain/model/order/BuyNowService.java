package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.DomainService;
import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.core.domain.model.product.Product;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@DomainService
@RequiredArgsConstructor
public class BuyNowService {

    private final CustomerHaveFreeShippingSpecification freeShippingSpecification;

    public Order buyNow(final Product product,
                        final Customer customer,
                        final Billing billing,
                        final Shipping shipping,
                        final Quantity quantity,
                        final PaymentMethod paymentMethod,
                        @Nullable
                        final CreditCardId creditCardId) {
        product.checkOutOfStock();
        return buildOrder(product, customer, billing, shipping, quantity, paymentMethod, creditCardId);
    }

    private Order buildOrder(final Product product,
                             final Customer customer,
                             final Billing billing,
                             final Shipping shipping,
                             final Quantity quantity,
                             final PaymentMethod paymentMethod,
                             @Nullable
                             final CreditCardId creditCardId) {
        final var order = Order.draft(customer.id());
        order.changeBilling(billing);
        order.changePaymentMethod(paymentMethod, creditCardId);
        order.addItem(product, quantity);

        if (haveFreeShipping(customer)) {
            order.changeShipping(shipping.toBuilder().cost(Money.ZERO).build());
        } else {
            order.changeShipping(shipping);
        }

        order.place();
        return order;
    }

    private boolean haveFreeShipping(final Customer customer) {
        return freeShippingSpecification.isSatisfiedBy(customer);
    }

}
