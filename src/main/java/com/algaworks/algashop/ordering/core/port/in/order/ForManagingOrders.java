package com.algaworks.algashop.ordering.core.port.in.order;

public interface ForManagingOrders {

    void markAsPaid(Long rawOrderId);


    void markAsReady(Long rawOrderId);


    void cancel(Long rawOrderId);
}
