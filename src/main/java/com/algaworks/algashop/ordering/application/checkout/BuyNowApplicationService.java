package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.BuyNowService;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyNowApplicationService {

    private final BuyNowService service;
    private final ProductCatalogService productCatalogService;
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    private final Orders orders;
    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler  billingInputDisassembler;

    @Transactional
    public String buyNow(final BuyNowInput input){
        final var product = findProduct(new ProductId(input.getProductId()));
        final var customerId = new CustomerId(input.getCustomerId());
        final var billing = billingInputDisassembler.toDomainModel(input.getBilling());
        final var calculateResult = calculateShippingCost(input.getShipping());
        final var shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), calculateResult);
        final var quantity = new Quantity(input.getQuantity());
        final var paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());
        final var order = service.buyNow(product, customerId, billing, shipping, quantity, paymentMethod);
        orders.add(order);
        return order.id().toString();
    }

    private Product findProduct(final ProductId productId){
        return productCatalogService.ofId(productId).orElseThrow(ProductNotFoundException::new);
    }

    private ShippingCostService.CalculationResult calculateShippingCost(final ShippingInput shippingInput){
        final var origin = originAddressService.originAddress().zipCode();
        final var destination = new ZipCode(shippingInput.getAddress().getZipCode());
        final var calculateRequest = new ShippingCostService.CalculationRequest(origin, destination);
        return shippingCostService.calculate(calculateRequest);
    }

}
