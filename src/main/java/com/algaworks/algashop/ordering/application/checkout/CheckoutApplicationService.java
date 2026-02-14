package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.CheckoutService;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFound;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Service
public class CheckoutApplicationService {

    private final ShoppingCarts shoppingCarts;
    private final Orders orders;
    private final Customers customers;
    private final CheckoutService service;
    private final OriginAddressService originAddressService;
    private final ShippingCostService shippingCostService;
    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler  billingInputDisassembler;

    public String checkout(final CheckoutInput input){
        requireNonNull(input);

        final var shoppingCart = shoppingCarts.ofId(new ShoppingCartId(input.getShoppingCartId()))
                .orElseThrow(ShoppingCartNotFound::new);
        final var customer = customers.ofId(shoppingCart.customerId())
                .orElseThrow(CustomerNotFoundException::new);
        final var billing = billingInputDisassembler.toDomainModel(input.getBilling());
        final var calculateResult = shippingDeliveryInfo(input.getShipping().getAddress().getZipCode());
        final var shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), calculateResult);
        final var paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());
        final var order = service.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

        orders.add(order);
        shoppingCart.empty();
        shoppingCarts.add(shoppingCart);
        return order.id().toString();
    }

    private ShippingCostService.CalculationResult shippingDeliveryInfo(final String zipCodeDestination){
        final var origin = originAddressService.originAddress().zipCode();
        final var destination = new ZipCode(zipCodeDestination);
        final var calculationRequest = new ShippingCostService.CalculationRequest(origin, destination);
        return shippingCostService.calculate(calculationRequest);
    }

}
