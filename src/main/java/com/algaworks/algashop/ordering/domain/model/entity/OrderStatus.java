package com.algaworks.algashop.ordering.domain.model.entity;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public enum OrderStatus {
    DRAFT(Collections.emptyList()),
    PLACED(List.of(DRAFT)),
    PAID(List.of(PLACED)),
    READY(List.of(PAID)),
    CANCELED(List.of(DRAFT, PLACED, PAID, READY));

    private final List<OrderStatus> previousStatuses;

    public boolean canChangeTo(final OrderStatus newStatus) {
        return newStatus.previousStatuses.contains(this);
    }

    public boolean canNotChangeTo(final OrderStatus newStatus) {
        return !canChangeTo(newStatus);
    }

}
