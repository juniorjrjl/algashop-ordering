package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.common.CommonDisassembler;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.requireNonNull;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@AnnotateWith(NullMarked.class)
@Mapper(componentModel = SPRING)
public abstract class ShippingInputDisassembler {

    @Nullable
    protected CommonDisassembler commonDisassembler;

    @Autowired
    public void setCommonDisassembler(final CommonDisassembler commonDisassembler) {
        this.commonDisassembler = commonDisassembler;
    }

    public CommonDisassembler getCommonDisassembler() {
        return requireNonNull(commonDisassembler, "commonDisassembler must be injected by Spring");
    }

    @Mapping(target = "cost", source = "calculationResult.cost")
    @Mapping(target = "expectedDate", source = "calculationResult.expectedDate")
    @Mapping(target = "address", expression = "java(getCommonDisassembler().toAddress(input.getAddress()))")
    public abstract Shipping toDomainModel(final ShippingInput input,
                                           final ShippingCostService.CalculationResult calculationResult);

    @Mapping(target = "fullName",
            expression = "java(getCommonDisassembler().toFullName(recipient.getFirstName(), recipient.getLastName()))")
    @Mapping(target = "document", expression = "java(getCommonDisassembler().toDocument(recipient.getDocument()))")
    @Mapping(target = "phone", expression = "java(getCommonDisassembler().toPhone(recipient.getPhone()))")
    protected abstract Recipient toDomainModel(final RecipientData recipient);

}
