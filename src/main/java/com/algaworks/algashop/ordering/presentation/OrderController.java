package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/{version}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderQueryService queryService;

    @GetMapping("/{id}")
    public OrderDetailOutput findById(@PathVariable String id) {
        return queryService.findById(id);
    }

}
