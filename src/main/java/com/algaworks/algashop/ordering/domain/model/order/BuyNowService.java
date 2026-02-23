package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class BuyNowService {

    private final CustomerHaveFreeShippingSpecification freeShippingSpecification;

    public Order buyNow(final Product product,
                        final Customer customer,
                        final Billing billing,
                        final Shipping shipping,
                        final Quantity quantity,
                        final PaymentMethod paymentMethod){
        product.checkOutOfStock();
        return buildOrder(product, customer, billing, shipping, quantity, paymentMethod);
    }

    private Order buildOrder(final Product product,
                             final Customer customer,
                             final Billing billing,
                             final Shipping shipping,
                             final Quantity quantity,
                             final PaymentMethod paymentMethod) {
        final var order = Order.draft(customer.id());
        order.changeBilling(billing);
        order.changePaymentMethod(paymentMethod);
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
