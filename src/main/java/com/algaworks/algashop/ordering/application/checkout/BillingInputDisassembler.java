package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.common.CommonDisassembler;
import com.algaworks.algashop.ordering.domain.model.order.Billing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public abstract class BillingInputDisassembler {

    protected CommonDisassembler commonDisassembler;

    @Autowired
    public void setCommonAssembler(final CommonDisassembler commonDisassembler) {
        this.commonDisassembler = commonDisassembler;
    }

    @Mapping(
            target = "fullName",
            expression = "java(commonDisassembler.toFullName(billingData.getFirstName(), billingData.getLastName()))"
    )
    @Mapping(target = "document", expression = "java(commonDisassembler.toDocument(billingData.getDocument()))")
    @Mapping(target = "phone", expression = "java(commonDisassembler.toPhone(billingData.getPhone()))")
    @Mapping(target = "email", expression = "java(commonDisassembler.toEmail(billingData.getEmail()))")
    @Mapping(target = "address", expression = "java(commonDisassembler.toAddress(billingData.getAddress()))")
    public abstract Billing toDomainModel(final BillingData billingData);

}
