package com.algaworks.algashop.ordering.core.port.in.customer;

import java.util.UUID;

public interface ForAddingLoyaltyPoints {

    void addLoyaltyPoints(final UUID rawCustomerId, final String rawOrderId);
}
