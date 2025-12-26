package com.algaworks.algashop.ordering.domain.entity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static com.algaworks.algashop.ordering.domain.entity.OrderStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class OrderStatusTest {

    private static final List<Arguments> canChangeTo = List.of(
            Arguments.of(DRAFT, PLACED),
            Arguments.of(PLACED, PAID),
            Arguments.of(PAID, READY),
            Arguments.of(DRAFT, CANCELED),
            Arguments.of(PLACED, CANCELED),
            Arguments.of(PAID, CANCELED),
            Arguments.of(READY, CANCELED)
    );

    @ParameterizedTest
    @FieldSource
    void canChangeTo(final OrderStatus currentStatus, final OrderStatus newStatus) {
        assertThat(currentStatus.canChangeTo(newStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(names = {"PAID", "READY", "DRAFT"})
    void draftCanNotChangeTo(final OrderStatus newStatus) {
        assertThat(DRAFT.canNotChangeTo(newStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(names = {"DRAFT", "PLACED", "READY"})
    void placedCanNotChangeTo(final OrderStatus newStatus) {
        assertThat(PLACED.canNotChangeTo(newStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(names = {"DRAFT", "PLACED"})
    void paidCanNotChangeTo(final OrderStatus newStatus) {
        assertThat(PLACED.canNotChangeTo(newStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(names = {"CANCELED"}, mode = EXCLUDE)
    void readyCanNotChangeTo(final OrderStatus newStatus) {
        assertThat(READY.canNotChangeTo(newStatus)).isTrue();
    }

    @ParameterizedTest
    @EnumSource(names = {"CANCELED"})
    void canceledCanNotChangeTo(final OrderStatus newStatus) {
        assertThat(CANCELED.canNotChangeTo(newStatus)).isTrue();
    }

}