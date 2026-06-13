package com.algaworks.algashop.ordering.infrastructure.adapter.out.web.shipping.client.rapidex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCostResponse {

    private String deliveryCost;
    private Long estimatedDaysToDeliver;

}
