package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import lombok.Builder;

import static java.util.Objects.requireNonNull;

@Builder
public record Billing(FullName fullName,
                      Document document,
                      Phone phone,
                      Address address,
                      Email email) {

    public Billing {
        requireNonNull(fullName);
        requireNonNull(document);
        requireNonNull(phone);
        requireNonNull(address);
        requireNonNull(email);
    }

}
