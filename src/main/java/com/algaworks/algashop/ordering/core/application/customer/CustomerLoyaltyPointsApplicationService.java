package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerLoyaltyPointsService;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.port.in.customer.ForAddingLoyaltyPoints;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomerLoyaltyPointsApplicationService implements ForAddingLoyaltyPoints {

    private final CustomerLoyaltyPointsService service;
    private final Customers customers;
    private final Orders orders;

    @Transactional
    @Override
    public void addLoyaltyPoints(final UUID rawCustomerId, final String rawOrderId){
        final var customer = customers.ofId(new CustomerId(rawCustomerId))
                .orElseThrow(CustomerNotFoundException::new);
        final var order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(OrderNotFoundException::new);
        service.addPoints(customer, order);
        customers.add(customer);
    }

}
