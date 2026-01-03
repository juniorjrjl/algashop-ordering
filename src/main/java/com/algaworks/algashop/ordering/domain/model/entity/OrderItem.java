package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import lombok.Builder;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class OrderItem {

    private OrderItemId id;
    private OrderId orderId;
    private ProductId productId;
    private ProductName productName;
    private Money price;
    private Quantity quantity;
    private Money totalAmount;

    @Builder(builderClassName = "ExistingOrderItemBuilder", builderMethodName = "existing")
    private OrderItem(final OrderItemId id,
                      final OrderId orderId,
                      final ProductId productId,
                      final ProductName productName,
                      final Money price,
                      final Quantity quantity,
                      final Money totalAmount) {
        this.setId(id);
        this.setOrderId(orderId);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setTotalAmount(totalAmount);
    }

    @Builder(builderClassName = "BrandNewOrderItemBuilder", builderMethodName = "brandNew")
    private static OrderItem createBrandNew(final OrderId orderId,
                                            final Product product,
                                            final Quantity quantity) {
        requireNonNull(orderId);
        requireNonNull(product);
        requireNonNull(quantity);
        final var orderItem = new OrderItem(
                new OrderItemId(),
                orderId,
                product.id(),
                product.name(),
                product.price(),
                quantity,
                Money.ZERO
        );
        orderItem.recalculateTotals();
        return orderItem;
    }

    void changeQuantity(final Quantity quantity) {
        requireNonNull(quantity);
        this.setQuantity(quantity);
        this.recalculateTotals();
    }

    public OrderItemId id() {
        return id;
    }

    public OrderId orderId() {
        return orderId;
    }

    public ProductId productId() {
        return productId;
    }

    public ProductName productName() {
        return productName;
    }

    public Money price() {
        return price;
    }

    public Quantity quantity() {
        return quantity;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    private void recalculateTotals() {
        this.setTotalAmount(this.price.multiply(this.quantity));
    }

    private void setId(final OrderItemId id) {
        requireNonNull(id);
        this.id = id;
    }

    private void setOrderId(final OrderId orderId) {
        requireNonNull(orderId);
        this.orderId = orderId;
    }

    private void setProductId(final ProductId productId) {
        requireNonNull(productId);
        this.productId = productId;
    }

    private void setProductName(final ProductName productName) {
        requireNonNull(productName);
        this.productName = productName;
    }

    private void setPrice(final Money price) {
        requireNonNull(price);
        this.price = price;
    }

    private void setQuantity(final Quantity quantity) {
        requireNonNull(quantity);
        this.quantity = quantity;
    }

    private void setTotalAmount(final Money totalAmount) {
        requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof OrderItem orderItem)) return false;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
