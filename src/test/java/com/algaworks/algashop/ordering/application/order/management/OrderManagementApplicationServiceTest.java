package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderCanceledInput;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderPaidInput;
import com.algaworks.algashop.ordering.application.order.notification.NotifyOrderReadyInput;
import com.algaworks.algashop.ordering.application.order.notification.OrderNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderCanceledEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderPaidEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.CANCELED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.PAID;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.PLACED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
class OrderManagementApplicationServiceTest  extends AbstractApplicationTest {

    private final Customers customers;
    private final Orders orders;
    private final OrderManagementApplicationService service;

    @MockitoSpyBean
    private OrderEventListener listener;

    @MockitoSpyBean
    private OrderNotificationApplicationService notificationApplicationService;

    @MockitoSpyBean
    private CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    private Customer customer;

    @Autowired
    public OrderManagementApplicationServiceTest(final JdbcTemplate jdbcTemplate,
                                                 final Customers customers,
                                                 final Orders orders,
                                                 final OrderManagementApplicationService service) {
        super(jdbcTemplate);
        this.customers = customers;
        this.orders = orders;
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
    }

    @ParameterizedTest
    @EnumSource(names = {"CANCELED"}, mode = EXCLUDE)
    void shouldCancel(final OrderStatus orderStatus) {
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .withOrderStatus(() -> orderStatus)
                .buildExisting();
        orders.add(order);
        service.cancel(order.id().value().toLong());
        final var actual = orders.ofId(order.id()).orElseThrow();
        assertThat(actual.isCanceled()).isTrue();
        verify(listener).listen(any(OrderCanceledEvent.class));
        verify(notificationApplicationService).notifyOrderCanceled(any(NotifyOrderCanceledInput.class));
    }

    @Test
    void givenCanceledOrderWhenCancelThenThrowException() {
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .withOrderStatus(() -> CANCELED)
                .buildExisting();
        orders.add(order);
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> service.cancel(order.id().value().toLong()));
        verifyNoInteractions(listener, notificationApplicationService);
    }


    @Test
    void shouldMarkAsReady() {
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .withOrderStatus(() -> PAID)
                .buildExisting();
        orders.add(order);
        service.markAsReady(order.id().value().toLong());
        final var actual = orders.ofId(order.id()).orElseThrow();
        assertThat(actual.isReady()).isTrue();
        verify(listener).listen(any(OrderReadyEvent.class));
        verify(notificationApplicationService).notifyOrderReady(any(NotifyOrderReadyInput.class));
        verify(customerLoyaltyPointsApplicationService).addLoyaltyPoints(
                any(UUID.class),
                any(String.class)
        );
    }

    @ParameterizedTest
    @EnumSource(names = {"PAID"}, mode = EXCLUDE)
    void givenOrderCanNotBeChangeToReadyWhenMarkAsReadyThenThrowException(final OrderStatus orderStatus) {
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .withOrderStatus(() -> orderStatus)
                .buildExisting();
        orders.add(order);
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> service.markAsReady(order.id().value().toLong()));
        verifyNoInteractions(listener, notificationApplicationService);
    }

    @Test
    void shouldMarkAsPaid() {
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .withOrderStatus(() -> PLACED)
                .buildExisting();
        orders.add(order);
        service.markAsPaid(order.id().value().toLong());
        final var actual = orders.ofId(order.id()).orElseThrow();
        assertThat(actual.isPaid()).isTrue();
        verify(listener).listen(any(OrderPaidEvent.class));
        verify(notificationApplicationService).notifyOrderPaid(any(NotifyOrderPaidInput.class));
    }

    @ParameterizedTest
    @EnumSource(names = {"PLACED"}, mode = EXCLUDE)
    void givenOrderCanNotBeChangeToPaidWhenMarkAsReadyThenThrowException(final OrderStatus orderStatus) {
        final var order = OrderDataBuilder.builder()
                .withCustomerId(() -> customer.id())
                .withOrderStatus(() -> orderStatus)
                .buildExisting();
        orders.add(order);
        assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() -> service.markAsPaid(order.id().value().toLong()));
        verifyNoInteractions(listener, notificationApplicationService);
    }

}