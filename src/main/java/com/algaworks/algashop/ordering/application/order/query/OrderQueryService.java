package com.algaworks.algashop.ordering.application.order.query;

import org.springframework.data.domain.Page;

public interface OrderQueryService {

    OrderDetailOutput findById(final String id);

    Page<OrderSummaryOutput> filter(final OrderFilter filter);

}
