package com.algaworks.algashop.ordering.application.order.notification;

public interface OrderNotificationApplicationService {

    void notifyOrderPlaced(final NotifyOrderPlacedInput input);

    void notifyOrderPaid(final NotifyOrderPaidInput input);

    void notifyOrderReady(final NotifyOrderReadyInput input);

    void notifyOrderCanceled(final NotifyOrderCanceledInput input);

}
