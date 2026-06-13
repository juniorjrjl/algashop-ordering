package com.algaworks.algashop.ordering.core.port.in.order;

import com.algaworks.algashop.ordering.core.port.in.common.AddressData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingInput {

    private RecipientData recipient;
    private AddressData address;

}
