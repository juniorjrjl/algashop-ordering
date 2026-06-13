package com.algaworks.algashop.ordering.infrastructure.adapter.in.web.order;

import com.algaworks.algashop.ordering.core.port.in.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.core.application.checkout.CheckoutApplicationService;
import com.algaworks.algashop.ordering.core.port.in.checkout.CheckoutInput;
import com.algaworks.algashop.ordering.core.port.in.checkout.ForBuyingProduct;
import com.algaworks.algashop.ordering.core.port.out.order.OrderDetailOutput;
import com.algaworks.algashop.ordering.core.port.in.order.OrderFilter;
import com.algaworks.algashop.ordering.core.port.out.order.ForObtainingOrder;
import com.algaworks.algashop.ordering.core.port.out.order.OrderSummaryOutput;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.infrastructure.adapter.in.web.PageModel;
import com.algaworks.algashop.ordering.infrastructure.adapter.in.web.exceptionhandler.UnprocessableEntityException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = "/api/{version}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ForObtainingOrder forObtaining;
    private final CheckoutApplicationService checkoutApplicationService;
    private final ForBuyingProduct forBuyingProduct;

    @GetMapping("/{id}")
    public OrderDetailOutput findById(@PathVariable String id) {
        return forObtaining.findById(id);
    }

    @GetMapping
    public PageModel<OrderSummaryOutput> filter(final OrderFilter filter) {
        return PageModel.of(forObtaining.filter(filter));
    }

    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    @ResponseStatus(CREATED)
    public OrderDetailOutput createWithProduct(@Valid @RequestBody final BuyNowInput input) {
        final String orderId;
        try {
            orderId = forBuyingProduct.buyNow(input);
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        }
        return forObtaining.findById(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
    @ResponseStatus(CREATED)
    public OrderDetailOutput createWithShoppingCart(@Valid @RequestBody final CheckoutInput input) {
        final String orderId;
        try {
            orderId = checkoutApplicationService.checkout(input);
        } catch (CustomerNotFoundException | ShoppingCartNotFoundException e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        }
        return forObtaining.findById(orderId);
    }

}
