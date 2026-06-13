package com.algaworks.algashop.ordering.core.port.out.order;

import com.algaworks.algashop.ordering.core.port.in.order.OrderFilter;
import org.springframework.data.domain.Page;

public interface ForObtainingOrder {

    OrderDetailOutput findById(final String id);

    Page<OrderSummaryOutput> filter(final OrderFilter filter);

}
