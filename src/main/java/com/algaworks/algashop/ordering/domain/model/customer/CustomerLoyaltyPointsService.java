package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;

import static java.util.Objects.requireNonNull;

@DomainService
public class CustomerLoyaltyPointsService {

    private static final LoyaltyPoints BASE_POINTS = LoyaltyPoints.BASE_POINTS;
    private static final Money expectedToGivePoints = new Money("1000");

    public void addPoints(final Customer customer, final Order order) {
        requireNonNull(customer);
        requireNonNull(order);
        if (!customer.id().equals(order.customerId())) {
            throw new OrderNotBelongsToCustomerException();
        }
        if (!order.isReady()){
            throw new CantAddLoyaltyPointsOrderIsNotReady();
        }
        customer.addLoyaltyPoints(calculatePoints(order));
    }

    private static LoyaltyPoints calculatePoints(final Order order) {
        if (shouldGivePointsByAmount(order.totalAmount())){
            final var result= order.totalAmount().divide(expectedToGivePoints);
            return new LoyaltyPoints(result.value().intValue() * BASE_POINTS.value());
        }
        
        return LoyaltyPoints.ZERO;
    }

    private static boolean shouldGivePointsByAmount(final Money amount) {
        return amount.isGreaterThanOrEqualTo(expectedToGivePoints);
    }

}
