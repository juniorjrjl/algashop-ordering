package com.algaworks.algashop.ordering.infrastructure.shipping.client.rapidex;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@HttpExchange(accept = APPLICATION_JSON_VALUE)
public interface RapiDexAPIClient {

    @PostExchange("/api/delivery-cost")
    DeliveryCostResponse calculate(@RequestBody final DeliveryCostRequest request);

}
