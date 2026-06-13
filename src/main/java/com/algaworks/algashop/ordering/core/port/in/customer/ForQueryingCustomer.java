package com.algaworks.algashop.ordering.core.port.in.customer;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ForQueryingCustomer {

    CustomerOutput findById(final UUID customerId);

    Page<CustomerSummaryOutput> filter(final CustomerFilter filter);

}
