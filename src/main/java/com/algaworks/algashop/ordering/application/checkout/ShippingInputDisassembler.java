package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.common.CommonDisassembler;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public abstract class ShippingInputDisassembler {

    protected CommonDisassembler commonDisassembler;

    @Autowired
    public void setCommonDisassembler(final CommonDisassembler commonDisassembler) {
        this.commonDisassembler = commonDisassembler;
    }

    @Mapping(target = "cost", source = "calculationResult.cost")
    @Mapping(target = "expectedDate", source = "calculationResult.expectedDate")
    @Mapping(target = "address", expression = "java(commonDisassembler.toAddress(input.getAddress()))")
    public abstract Shipping toDomainModel(final ShippingInput input,
                                    final ShippingCostService.CalculationResult calculationResult);

    @Mapping(target = "fullName",
            expression = "java(commonDisassembler.toFullName(recipient.getFirstName(), recipient.getLastName()))")
    @Mapping(target = "document", expression = "java(commonDisassembler.toDocument(recipient.getDocument()))")
    @Mapping(target = "phone", expression = "java(commonDisassembler.toPhone(recipient.getPhone()))")
    protected abstract Recipient toDomainModel(final RecipientData recipient);

}
