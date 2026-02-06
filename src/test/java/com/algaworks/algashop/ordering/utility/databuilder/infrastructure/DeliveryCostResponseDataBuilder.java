package com.algaworks.algashop.ordering.utility.databuilder.infrastructure;

import com.algaworks.algashop.ordering.infrastructure.shipping.client.rapidex.DeliveryCostResponse;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class DeliveryCostResponseDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private static final ObjectMapper mapper = new ObjectMapper();

    @With
    private Supplier<String> deliveryCost = () -> customFaker.common().money().toString();
    @With
    private Supplier<Long> estimatedDaysToDeliver = () -> customFaker.number().numberBetween(1L, 18L);

    public static DeliveryCostResponseDataBuilder builder(){
        return new DeliveryCostResponseDataBuilder();
    }

    public DeliveryCostResponse build(){
        return new DeliveryCostResponse(deliveryCost.get(), estimatedDaysToDeliver.get());
    }

    public String buildJson() throws JsonProcessingException {
        return mapper.writeValueAsString(this.build());
    }

}
