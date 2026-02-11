package com.algaworks.algashop.ordering.infrastructure.listener.order;

import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderCanceledInput;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderPaidInput;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderPlacedInput;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderReadyInput;
import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.order.OrderCanceledEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderPaidEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderPlacedEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderNotificationApplicationService notificationApplicationService;

    @EventListener(OrderPlacedEvent.class)
    public void listen(final OrderPlacedEvent event) {
        final var input = new NotifyOrderPlacedInput(
                event.orderId().value(),
                event.customerId().value(),
                event.placedAt()
        );
        notificationApplicationService.notifyOrderPlaced(input);
    }

    @EventListener(OrderPaidEvent.class)
    public void listen(final OrderPaidEvent event) {
        final var input = new NotifyOrderPaidInput(
                event.orderId().value(),
                event.customerId().value(),
                event.paidAt()
        );
        notificationApplicationService.notifyOrderPaid(input);
    }

    @EventListener(OrderReadyEvent.class)
    public void listen(final OrderReadyEvent event) {
        final var input = new NotifyOrderReadyInput(
                event.orderId().value(),
                event.customerId().value(),
                event.readyAt()
        );
        notificationApplicationService.notifyOrderReady(input);
    }

    @EventListener(OrderCanceledEvent.class)
    public void listen(final OrderCanceledEvent event) {
        final var input = new NotifyOrderCanceledInput(
                event.orderId().value(),
                event.customerId().value(),
                event.canceledAt()
        );
        notificationApplicationService.notifyOrderCanceled(input);
    }

}
