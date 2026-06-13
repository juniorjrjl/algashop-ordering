package com.algaworks.algashop.ordering.core.port.out.customer;

import com.algaworks.algashop.ordering.core.port.in.customer.CustomerFilter;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerOutput;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerSummaryOutput;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ForObtainingCustomer {

    CustomerOutput findById(final UUID customerId);

    Page<CustomerSummaryOutput> filter(final CustomerFilter filter);

}
