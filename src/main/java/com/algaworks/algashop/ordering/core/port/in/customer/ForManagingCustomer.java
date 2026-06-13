package com.algaworks.algashop.ordering.core.port.in.customer;

import java.util.UUID;

public interface ForManagingCustomer {

    UUID create(final CustomerInput input);

    void update(final UUID rawId, final CustomerUpdateInput input);

    void archive(final UUID rawId);

    void changeEmail(final UUID rawId, final String email);
}
