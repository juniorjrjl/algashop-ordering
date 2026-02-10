package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.common.CommonDisassembler;
import com.algaworks.algashop.ordering.domain.model.order.Billing;
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
public abstract class BillingInputDisassembler {

    @Nullable
    private CommonDisassembler commonDisassembler;

    @Autowired
    public void setCommonAssembler(final CommonDisassembler commonDisassembler) {
        this.commonDisassembler = commonDisassembler;
    }

    public CommonDisassembler getCommonDisassembler() {
        return requireNonNull(commonDisassembler, "commonDisassembler must be injected by Spring");
    }

    @Mapping(
            target = "fullName",
            expression = "java(getCommonDisassembler().toFullName(billingData.getFirstName(), billingData.getLastName()))"
    )
    @Mapping(target = "document", expression = "java(getCommonDisassembler().toDocument(billingData.getDocument()))")
    @Mapping(target = "phone", expression = "java(getCommonDisassembler().toPhone(billingData.getPhone()))")
    @Mapping(target = "email", expression = "java(getCommonDisassembler().toEmail(billingData.getEmail()))")
    @Mapping(target = "address", expression = "java(getCommonDisassembler().toAddress(billingData.getAddress()))")
    public abstract Billing toDomainModel(final BillingData billingData);

}
