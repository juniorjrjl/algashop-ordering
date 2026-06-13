package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.port.in.customer.CustomerFilter;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerOutput;
import com.algaworks.algashop.ordering.core.port.in.customer.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.core.port.in.customer.ForQueryingCustomer;
import com.algaworks.algashop.ordering.core.port.out.customer.ForObtainingCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerQueryService implements ForQueryingCustomer {

    private final ForObtainingCustomer forObtainingCustomer;

    @Override
    public CustomerOutput findById(final UUID customerId) {
        return forObtainingCustomer.findById(customerId);
    }

    @Override
    public Page<CustomerSummaryOutput> filter(final CustomerFilter filter) {
        return forObtainingCustomer.filter(filter);
    }

}
