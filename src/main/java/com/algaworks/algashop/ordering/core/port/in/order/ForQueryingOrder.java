package com.algaworks.algashop.ordering.core.port.in.order;

import com.algaworks.algashop.ordering.core.port.out.order.OrderDetailOutput;
import com.algaworks.algashop.ordering.core.port.out.order.OrderSummaryOutput;
import org.springframework.data.domain.Page;

public interface ForQueryingOrder {

    OrderDetailOutput findById(final String id);

    Page<OrderSummaryOutput> filter(final OrderFilter filter);

}
