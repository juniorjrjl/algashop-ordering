package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.Repository;
import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;

import java.time.Year;
import java.util.List;

public interface Orders extends Repository<Order, OrderId> {

    List<Order> placedByCustomerInYear(final CustomerId customerId, final Year year);

    Long salesQuantityByCustomerInYear(final CustomerId customerId, final Year year);

    Money totalSoldForCustomer(final CustomerId customerId);

}
