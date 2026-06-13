package com.algaworks.algashop.ordering.infrastructure.adapter.out.web.product.client.http;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface ProductCatalogAPIClient {

    @GetExchange(value = "/api/v1/products/{productId}", accept = APPLICATION_JSON_VALUE)
    ProductResponse getById(@PathVariable final UUID productId);

}
