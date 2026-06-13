package com.algaworks.algashop.ordering.infrastructure.adapter.in.web.customer;

import com.algaworks.algashop.ordering.core.port.in.customer.CustomerInput;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerUpdateInput;
import com.algaworks.algashop.ordering.core.port.in.customer.ForManagingCustomer;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerFilter;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerOutput;
import com.algaworks.algashop.ordering.core.port.in.customer.ForQueryingCustomer;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartOutput;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ForQueryingShoppingCart;
import com.algaworks.algashop.ordering.infrastructure.adapter.in.web.PageModel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/{version}/customers")
public class CustomerController {

    private final ForManagingCustomer forManaging;
    private final ForQueryingCustomer forQuerying;
    private final ForQueryingShoppingCart forQueryingShoppingCart;

    @PostMapping(version = "1")
    @ResponseStatus(CREATED)
    public CustomerOutput create(@RequestBody @Valid final CustomerInput input,
                                 final HttpServletResponse response) {
        final var customerId = forManaging.create(input);
        final var builder = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(customerId);
        response.setHeader("Location", builder.toUriString());
        return forQuerying.findById(customerId);
    }

    @PutMapping(version = "1", path = "/{id}")
    public CustomerOutput update(@PathVariable final UUID id,
                                 @RequestBody @Valid final CustomerUpdateInput input){
        forManaging.update(id, input);
        return forQuerying.findById(id);
    }

    @DeleteMapping(version = "1", path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable final UUID id){
        forManaging.archive(id);
    }

    @GetMapping(version = "1")
    public PageModel<CustomerSummaryOutput> findAll(final CustomerFilter filter) {
        return PageModel.of(forQuerying.filter(filter));
    }

    @GetMapping(version = "1", path = "/{id}")
    public CustomerOutput findById(@PathVariable final UUID id) {
        return forQuerying.findById(id);
    }

    @GetMapping(version = "1", path = "/{id}/shopping-cart")
    public ShoppingCartOutput findShoppingCartById(@PathVariable final UUID id) {
        return forQueryingShoppingCart.findByCustomerId(id);
    }

}
