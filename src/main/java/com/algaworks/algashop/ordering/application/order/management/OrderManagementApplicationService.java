package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderManagementApplicationService {

    private final Orders orders;

    @Transactional
    public void markAsPaid(final Long rawOrderId){
        final var order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(OrderNotFoundException::new);
        order.markAsPaid();
        orders.add(order);
    }

    @Transactional
    public void markAsReady(final Long rawOrderId){
        final var order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(OrderNotFoundException::new);
        order.markAsReady();
        orders.add(order);
    }

    @Transactional
    public void cancel(final Long rawOrderId){
        final var order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(OrderNotFoundException::new);
        order.cancel();
        orders.add(order);
    }

}
