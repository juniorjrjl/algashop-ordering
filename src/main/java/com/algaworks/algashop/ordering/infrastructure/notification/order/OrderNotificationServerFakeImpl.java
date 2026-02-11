package com.algaworks.algashop.ordering.infrastructure.notification.order;

import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderCanceledInput;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderPaidInput;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderPlacedInput;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderReadyInput;
import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderNotificationServerFakeImpl implements OrderNotificationApplicationService {

    @Override
    public void notifyOrderPlaced(final NotifyOrderPlacedInput input) {
        log.info("Order Placed: {}", input);
    }

    @Override
    public void notifyOrderPaid(final NotifyOrderPaidInput input) {
        log.info("Order Paid: {}", input);
    }

    @Override
    public void notifyOrderReady(final NotifyOrderReadyInput input) {
        log.info("Order Ready: {}", input);
    }

    @Override
    public void notifyOrderCanceled(final NotifyOrderCanceledInput input) {
        log.info("Order Canceled: {}", input);
    }
}
