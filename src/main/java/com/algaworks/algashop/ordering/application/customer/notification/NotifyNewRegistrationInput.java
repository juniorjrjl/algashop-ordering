package com.algaworks.algashop.ordering.application.customer.notification;

import java.util.UUID;

public record NotifyNewRegistrationInput(
        UUID customerId,
        String firstName,
        String email
) {}