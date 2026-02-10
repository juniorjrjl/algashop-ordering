package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.AggregateRoot;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import lombok.Builder;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

public class ShoppingCart implements AggregateRoot<ShoppingCartId> {

    private ShoppingCartId id;
    private CustomerId customerId;
    private Money totalAmount;
    private Quantity totalItems;
    private OffsetDateTime createdAt;
    private Set<ShoppingCartItem> items;

    @Nullable
    @Setter(PRIVATE)
    private Long version;

    public static ShoppingCart startShopping(final CustomerId customerId){
        return new ShoppingCart(
                new ShoppingCartId(),
                customerId,
                OffsetDateTime.now(),
                new HashSet<>(),
                null
        );
    }

    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    private ShoppingCart(final ShoppingCartId id,
                         final CustomerId customerId,
                         final OffsetDateTime createdAt,
                         final Set<ShoppingCartItem> items,
                         final Long version) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setCreatedAt(createdAt);
        this.setItems(items);
        this.setVersion(version);
        this.recalculateTotals();
    }

    public void addItem(final Product product, final Quantity quantity){
        requireNonNull(product);
        requireNonNull(quantity);

        this.tryFindItem(product.id())
                .ifPresentOrElse(
                        i -> {
                            i.refresh(product);
                            i.changeQuantity(quantity.add(i.quantity()));
                        },
                        () -> this.items.add(ShoppingCartItem.brandNew()
                                        .shoppingCartId(this.id)
                                        .product(product)
                                        .quantity(quantity)
                                .build())
                );
        this.recalculateTotals();
    }

    public void removeItem(final ShoppingCartItemId itemId){
        final var toRemove = findItem(itemId);
        final var updatedItems = this.items.stream()
                .filter(i -> !i.equals(toRemove))
                .collect(Collectors.toSet());
        this.setItems(updatedItems);
        this.recalculateTotals();
    }

    public void refreshItem(final Product product){
        final var item = findItem(product.id());
        item.refresh(product);
        this.recalculateTotals();
    }

    public void changeItemQuantity(final ShoppingCartItemId itemId, final Quantity quantity){
        requireNonNull(itemId);
        requireNonNull(quantity);

        final var item = findItem(itemId);
        item.changeQuantity(quantity);
        this.recalculateTotals();
    }

    public ShoppingCartItem findItem(final ShoppingCartItemId itemId) {
        return this.items.stream()
                .filter(i -> i.id().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainOrderItemException(this.id, itemId));
    }

    private Optional<ShoppingCartItem> tryFindItem(final ProductId productId) {
        return this.items.stream()
                .filter(i -> i.productId().equals(productId))
                .findFirst();
    }

    public ShoppingCartItem findItem(final ProductId productId) {
        return tryFindItem(productId)
                .orElseThrow(() -> new ShoppingCartDoesNotContainProductException(this.id, productId));
    }

    public void empty(){
        this.setTotalAmount(Money.ZERO);
        this.setTotalItems(Quantity.ZERO);
        this.setItems(new HashSet<>());
    }

    public boolean isEmpty(){
        return this.items.isEmpty() &&
                this.totalAmount.equals(Money.ZERO) &&
                this.totalItems.equals(Quantity.ZERO);
    }

    public boolean containsUnavailable(){
        return !this.items.stream().allMatch(ShoppingCartItem::isAvailable);
    }

    public ShoppingCartId id() {
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

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public Set<ShoppingCartItem> items() {
        return Collections.unmodifiableSet(items);
    }

    public Long version() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ShoppingCart that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private void recalculateTotals() {
        final var totalItemsAmount = this.items.stream()
                .map(ShoppingCartItem::totalAmount)
                .reduce(Money.ZERO, Money::add);

        final var totalItemsQuantity = this.items.stream()
                .map(ShoppingCartItem::quantity)
                .reduce(Quantity.ZERO, Quantity::add);

        this.setTotalAmount(totalItemsAmount);
        this.setTotalItems(totalItemsQuantity);
    }

    private void setId(final ShoppingCartId id) {
        this.id = requireNonNull(id);
    }

    private void setCustomerId(final CustomerId customerId) {
        this.customerId = requireNonNull(customerId);
    }

    private void setTotalAmount(final Money totalAmount) {
        this.totalAmount = requireNonNull(totalAmount);
    }

    private void setTotalItems(final Quantity totalItems) {
        this.totalItems = requireNonNull(totalItems);
    }

    private void setCreatedAt(final OffsetDateTime createdAt) {
        this.createdAt = requireNonNull(createdAt);
    }

    private void setItems(final Set<ShoppingCartItem> items) {
        this.items = requireNonNull(items);
    }

}
