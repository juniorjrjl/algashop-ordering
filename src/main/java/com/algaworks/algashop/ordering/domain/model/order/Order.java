package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.AggregateRoot;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.CANCELED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.DRAFT;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.PAID;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.PLACED;
import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.READY;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Builder(toBuilder = true)
public class Order implements AggregateRoot<OrderId> {

    private OrderId id;
    private CustomerId customerId;
    private Money totalAmount;
    private Quantity totalItems;
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime readyAt;
    private Billing billing;
    private Shipping shipping;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private Set<OrderItem> items;
    @Setter(PRIVATE)
    private Long version;

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(final OrderId id,
                 final CustomerId customerId,
                 final Money totalAmount,
                 final Quantity totalItems,
                 final OffsetDateTime placedAt,
                 final OffsetDateTime paidAt,
                 final OffsetDateTime canceledAt,
                 final OffsetDateTime readyAt,
                 final Billing billing,
                 final Shipping shipping,
                 final OrderStatus orderStatus,
                 final PaymentMethod paymentMethod,
                 final Set<OrderItem> items,
                 final Long version) {
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
        this.setItems(items);
        this.setVersion(version);
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
                new HashSet<>(),
                null
        );
    }

    public void addItem(final Product product,
                        final Quantity quantity){
        this.verifyIfChangeable();
        requireNonNull(product);
        requireNonNull(quantity);
        product.checkOutOfStock();
        var orderItem = OrderItem.brandNew()
                .orderId(this.id)
                .product(product)
                .quantity(quantity)
                .build();
        this.items.add(orderItem);
        this.recalculateTotals();
    }

    public void removeItem(final OrderItemId orderItemId){
        this.verifyIfChangeable();
        requireNonNull(orderItemId);
        final var orderItem = findOrderItem(orderItemId);
        final var currentItems = this.items.stream()
                .filter(i -> !i.equals(orderItem))
                .collect(Collectors.toSet());
        this.setItems(currentItems);

        this.recalculateTotals();
    }

    public void place(){
        this.verifyIfCanChangeToPlace();
        this.changeStatus(PLACED);
        this.setPlacedAt(OffsetDateTime.now());
    }

    public void markAsPaid() {
        this.changeStatus(PAID);
        this.setPaidAt(OffsetDateTime.now());
    }

    public void markAsReady() {
        this.changeStatus(READY);
        this.setReadyAt(OffsetDateTime.now());
    }

    public void cancel() {
        this.changeStatus(CANCELED);
        this.setCanceledAt(OffsetDateTime.now());
    }

    public void changePaymentMethod(final PaymentMethod newPaymentMethod){
        this.verifyIfChangeable();
        requireNonNull(newPaymentMethod);
        this.setPaymentMethod(newPaymentMethod);
    }

    public void changeBilling(final Billing billing){
        this.verifyIfChangeable();
        requireNonNull(billing);
        this.setBilling(billing);
    }

    public void changeShipping(final Shipping shipping){
        this.verifyIfChangeable();
        requireNonNull(shipping);
        if (shipping.expectedDate().isBefore(LocalDate.now())) {
            throw new OrderInvalidShippingDeliveryDateException(this.id);
        }

        this.setShipping(shipping);
    }

    public void changeItemQuantity(final OrderItemId orderItemId, final Quantity newQuantity){
        this.verifyIfChangeable();
        requireNonNull(orderItemId);
        requireNonNull(newQuantity);

        final var orderItem = findOrderItem(orderItemId);
        orderItem.changeQuantity(newQuantity);

        this.recalculateTotals();
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

    public boolean isReady(){
        return READY.equals(this.orderStatus);
    }

    public boolean isCanceled(){
        return CANCELED.equals(this.orderStatus);
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

    public Billing billing() {
        return billing;
    }

    public Shipping shipping() {
        return shipping;
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(items);
    }

    private void recalculateTotals() {
        final var totalItemsAmount = this.items.stream().map(OrderItem::totalAmount)
                .reduce(Money.ZERO, Money::add);

        final var totalItemsQuantity = this.items.stream().map(OrderItem::quantity)
                .reduce(Quantity.ZERO, Quantity::add);

        final var currentShippingCost = Optional.ofNullable(this.shipping())
                .map(Shipping::cost)
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

    private void verifyIfCanChangeToPlace(){
        if (isNull(this.shipping())){
            throw OrderCannotBePlacedException.noShippingInfo(this.id);
        }
        if (isNull(this.billing())){
            throw OrderCannotBePlacedException.noBillingInfo(this.id);
        }
        if (isNull(this.paymentMethod())){
            throw OrderCannotBePlacedException.noPaymentMethod(this.id);
        }
        if (isNull(this.items()) || this.items.isEmpty()){
            throw OrderCannotBePlacedException.noShippingInfo(this.id);
        }
    }

    private boolean isNotDraft(){
        return !isDraft();
    }

    private void verifyIfChangeable(){
        if (isNotDraft()){
            throw new OrderCannotBeEditedException(this.id, this.orderStatus);
        }
    }

    private OrderItem findOrderItem(final OrderItemId orderItemId){
        requireNonNull(orderItemId);
        return this.items.stream()
                .filter(i -> i.id().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderDoesNotContainOrderItemException(this.id, orderItemId));
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

    private void setBilling(final Billing billing) {
        this.billing = billing;
    }

    private void setShipping(final Shipping shipping) {
        this.shipping = shipping;
    }

    private void setOrderStatus(final OrderStatus orderStatus) {
        requireNonNull(orderStatus);
        this.orderStatus = orderStatus;
    }

    private void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private void setItems(final Set<OrderItem> items) {
        requireNonNull(items);
        this.items = items;
    }

    public Long version(){
        return this.version;
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
