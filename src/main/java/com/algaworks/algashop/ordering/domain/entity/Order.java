package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderCannotBePlacedException;
import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.BillingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.ShippingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.valueobject.id.ProductId;
import lombok.Builder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.algaworks.algashop.ordering.domain.entity.OrderStatus.DRAFT;
import static com.algaworks.algashop.ordering.domain.entity.OrderStatus.PAID;
import static com.algaworks.algashop.ordering.domain.entity.OrderStatus.PLACED;
import static java.util.Objects.requireNonNull;

@Builder(toBuilder = true)
public class Order {

    private OrderId id;
    private CustomerId customerId;
    private Money totalAmount;
    private Quantity totalItems;
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;
    private BillingInfo billing;
    private ShippingInfo shipping;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private Money shippingCost;
    private LocalDate expectedDeliveryDate;
    private Set<OrderItem> items;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(final OrderId id,
                 final CustomerId customerId,
                 final Money totalAmount,
                 final Quantity totalItems,
                 final OffsetDateTime placedAt,
                 final OffsetDateTime paidAt,
                 final OffsetDateTime canceledAt,
                 final OffsetDateTime readyAt,
                 final BillingInfo billing,
                 final ShippingInfo shipping,
                 final OrderStatus orderStatus,
                 final PaymentMethod paymentMethod,
                 final Money shippingCost,
                 final LocalDate expectedDeliveryDate,
                 final Set<OrderItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCanceledAt(canceledAt);
        this.setReadyAt(readyAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setOrderStatus(orderStatus);
        this.setPaymentMethod(paymentMethod);
        this.setShippingCost(shippingCost);
        this.setExpectedDeliveryDate(expectedDeliveryDate);
        this.setItems(items);
    }

    public static Order draft(final CustomerId customerId){
        return new Order(
                new OrderId(),
                customerId,
                Money.ZERO,
                Quantity.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                DRAFT,
                null,
                Money.ZERO,
                null,
                new HashSet<>()
        );
    }

    public void addItem(final ProductId productId,
                        final ProductName productName,
                        final Money price,
                        final Quantity quantity){
        var orderItem = OrderItem.brandNew()
                .orderId(this.id)
                .price(price)
                .quantity(quantity)
                .productName(productName)
                .productId(productId)
                .build();
        this.items.add(orderItem);
        this.recalculateTotals();
    }

    public void place(){
        requireNonNull(this.shipping);
        requireNonNull(this.billing);
        requireNonNull(this.expectedDeliveryDate);
        requireNonNull(this.shippingCost);
        requireNonNull(this.paymentMethod);
        if (this.items.isEmpty()) {
            throw new OrderCannotBePlacedException(this.id);
        }
        this.setPlacedAt(OffsetDateTime.now());
        this.changeStatus(PLACED);
    }

    public void markAsPaid() {
        this.setPaidAt(OffsetDateTime.now());
        this.changeStatus(PAID);
    }

    public void changePaymentMethod(final PaymentMethod newPaymentMethod){
        requireNonNull(newPaymentMethod);
        this.setPaymentMethod(newPaymentMethod);
    }

    public void changeBillingInfo(final BillingInfo billing){
        requireNonNull(billing);
        this.setBilling(billing);
    }

    public void changeShippingInfo(final ShippingInfo shipping,
                                   final Money shippingCost,
                                   final LocalDate expectedDeliveryDate){
        requireNonNull(shipping);
        requireNonNull(shippingCost);
        requireNonNull(expectedDeliveryDate);
        if (expectedDeliveryDate.isBefore(LocalDate.now())) {
            throw new OrderInvalidShippingDeliveryDateException(this.id);
        }

        this.setShipping(shipping);
        this.setShippingCost(shippingCost);
        this.setExpectedDeliveryDate(expectedDeliveryDate);
    }

    public boolean isDraft(){
        return DRAFT.equals(this.orderStatus);
    }

    public boolean isPlaced(){
        return PLACED.equals(this.orderStatus);
    }

    public boolean isPaid(){
        return PAID.equals(this.orderStatus);
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime placedAt() {
        return placedAt;
    }

    public OffsetDateTime paidAt() {
        return paidAt;
    }

    public OffsetDateTime canceledAt() {
        return canceledAt;
    }

    public OffsetDateTime readyAt() {
        return readyAt;
    }

    public BillingInfo billing() {
        return billing;
    }

    public ShippingInfo shipping() {
        return shipping;
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public Money shippingCost() {
        return shippingCost;
    }

    public LocalDate expectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(items);
    }

    private void recalculateTotals() {
        final var totalItemsAmount = this.items.stream().map(OrderItem::totalAmount)
                .reduce(Money.ZERO, Money::add);

        final var totalItemsQuantity = this.items.stream().map(OrderItem::quantity)
                .reduce(Quantity.ZERO, Quantity::add);

        final var currentShippingCost = Optional.ofNullable(this.shippingCost())
                .orElse(Money.ZERO);

        final var newTotalAmount = totalItemsAmount.add(currentShippingCost);
        this.setTotalAmount(newTotalAmount);
        this.setTotalItems(totalItemsQuantity);
    }

    private void changeStatus(final OrderStatus newStatus) {
        requireNonNull(newStatus);
        if (this.orderStatus.canNotChangeTo(newStatus)) {
            throw new OrderStatusCannotBeChangedException(
                    this.id(),
                    this.orderStatus,
                    newStatus
            );
        }
        this.setOrderStatus(newStatus);
    }

    private void setId(final OrderId id) {
        requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(final CustomerId customerId) {
        requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(final Money totalAmount) {
        requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(final Quantity totalItems) {
        requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setPlacedAt(final OffsetDateTime placedAt) {
        this.placedAt = placedAt;
    }

    private void setPaidAt(final OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    private void setCanceledAt(final OffsetDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    private void setReadyAt(final OffsetDateTime readyAt) {
        this.readyAt = readyAt;
    }

    private void setBilling(final BillingInfo billing) {
        this.billing = billing;
    }

    private void setShipping(final ShippingInfo shipping) {
        this.shipping = shipping;
    }

    private void setOrderStatus(final OrderStatus orderStatus) {
        requireNonNull(orderStatus);
        this.orderStatus = orderStatus;
    }

    private void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private void setShippingCost(final Money shippingCost) {
        this.shippingCost = shippingCost;
    }

    private void setExpectedDeliveryDate(final LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    private void setItems(final Set<OrderItem> items) {
        requireNonNull(items);
        this.items = items;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
