package com.algaworks.algashop.ordering.core.port.out.customer;

import java.util.UUID;

public record NotifyNewRegistrationInput(
        UUID customerId,
        String firstName,
        String email
) {}