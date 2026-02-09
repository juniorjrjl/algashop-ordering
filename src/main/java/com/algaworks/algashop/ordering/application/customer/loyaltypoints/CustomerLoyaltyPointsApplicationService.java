package com.algaworks.algashop.ordering.application.customer.loyaltypoints;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerLoyaltyPointsService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomerLoyaltyPointsApplicationService {

    private final CustomerLoyaltyPointsService service;
    private final Customers customers;
    private final Orders orders;

    @Transactional
    public void addLoyaltyPoints(@NonNull final UUID rawCustomerId, @NonNull final String rawOrderId){
        final var customer = customers.ofId(new CustomerId(rawCustomerId))
                .orElseThrow(CustomerNotFoundException::new);
        final var order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(OrderNotFoundException::new);
        service.addPoints(customer, order);
        customers.add(customer);
    }

}
