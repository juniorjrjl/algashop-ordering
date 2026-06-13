package com.algaworks.algashop.ordering.core.application.order;

import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.port.in.order.ForManagingOrders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderManagementApplicationService implements ForManagingOrders {

    private final Orders orders;

    @Transactional
    @Override
    public void markAsPaid(final Long rawOrderId){
        final var order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(OrderNotFoundException::new);
        order.markAsPaid();
        orders.add(order);
    }

    @Transactional
    @Override
    public void markAsReady(final Long rawOrderId){
        final var order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(OrderNotFoundException::new);
        order.markAsReady();
        orders.add(order);
    }

    @Transactional
    @Override
    public void cancel(final Long rawOrderId){
        final var order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(OrderNotFoundException::new);
        order.cancel();
        orders.add(order);
    }

}
