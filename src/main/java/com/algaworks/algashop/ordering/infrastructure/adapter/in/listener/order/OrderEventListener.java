package com.algaworks.algashop.ordering.infrastructure.adapter.in.listener.order;

import com.algaworks.algashop.ordering.core.domain.model.order.OrderCanceledEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderPaidEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderPlacedEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    @EventListener(OrderPlacedEvent.class)
    public void listen(final OrderPlacedEvent event) {

    }

    @EventListener(OrderPaidEvent.class)
    public void listen(final OrderPaidEvent event) {

    }

    @EventListener(OrderReadyEvent.class)
    public void listen(final OrderReadyEvent event) {

    }

    @EventListener(OrderCanceledEvent.class)
    public void listen(final OrderCanceledEvent event) {

    }

}
