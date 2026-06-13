package com.algaworks.algashop.ordering.core.application.order;

import com.algaworks.algashop.ordering.core.port.out.order.OrderDetailOutput;
import com.algaworks.algashop.ordering.core.port.in.order.OrderFilter;
import com.algaworks.algashop.ordering.core.port.out.order.OrderSummaryOutput;
import com.algaworks.algashop.ordering.core.port.in.order.ForQueryingOrder;
import com.algaworks.algashop.ordering.core.port.out.order.ForObtainingOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQueryService implements ForQueryingOrder {

    private final ForObtainingOrder forObtainingOrder;

    @Override
    public OrderDetailOutput findById(final String id) {
        return forObtainingOrder.findById(id);
    }

    @Override
    public Page<OrderSummaryOutput> filter(final OrderFilter filter) {
        return forObtainingOrder.filter(filter);
    }

}
