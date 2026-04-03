package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.algaworks.algashop.ordering.application.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.algaworks.algashop.ordering.application.checkout.CheckoutInput;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderFilter;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.application.order.query.OrderSummaryOutput;
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

    private final OrderQueryService queryService;
    private final CheckoutApplicationService checkoutApplicationService;
    private final BuyNowApplicationService buyNowApplicationService;

    @GetMapping("/{id}")
    public OrderDetailOutput findById(@PathVariable String id) {
        return queryService.findById(id);
    }

    @GetMapping
    public PageModel<OrderSummaryOutput> filter(final OrderFilter filter) {
        return PageModel.of(queryService.filter(filter));
    }

    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    @ResponseStatus(CREATED)
    public OrderDetailOutput createWithProduct(@Valid @RequestBody final BuyNowInput input) {
        final var orderId = buyNowApplicationService.buyNow(input);
        return queryService.findById(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
    @ResponseStatus(CREATED)
    public OrderDetailOutput createWithShoppingCart(@Valid @RequestBody final CheckoutInput input) {
        String orderId = checkoutApplicationService.checkout(input);
        return queryService.findById(orderId);
    }

}
