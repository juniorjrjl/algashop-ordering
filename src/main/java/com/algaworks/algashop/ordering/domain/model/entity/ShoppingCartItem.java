package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessage.QUANTITY_LESS_THAN;
import static java.util.Objects.requireNonNull;

public class ShoppingCartItem {

    private ShoppingCartItemId id;
    private ShoppingCartId shoppingCartId;
    private ProductId productId;
    private ProductName name;
    private Money price;
    private Quantity quantity;
    private Money totalAmount;
    private Boolean available;

    @Builder(builderClassName = "BrandNewShoppingCartItemBuilder", builderMethodName = "brandNew")
    public static ShoppingCartItem brandNewOne(final ShoppingCartId shoppingCartId,
                                               final Product product,
                                               final Quantity quantity){
        return new ShoppingCartItem(
                new ShoppingCartItemId(),
                shoppingCartId,
                product.id(),
                product.name(),
                product.price(),
                quantity,
                product.inStock()
        );
    }

    @Builder(builderClassName = "ExistingShoppingCartItemBuilder", builderMethodName = "existing")
    private ShoppingCartItem(final ShoppingCartItemId id,
                             final ShoppingCartId shoppingCartId,
                             final ProductId productId,
                             final ProductName name,
                             final Money price,
                             final Quantity quantity,
                             final Boolean available) {
        this.setId(id);
        this.setShoppingCartId(shoppingCartId);
        this.setProductId(productId);
        this.setName(name);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setAvailable(available);
        this.recalculateTotals();
    }

    public ShoppingCartItemId id() {
        return id;
    }

    public ShoppingCartId shoppingCartId() {
        return shoppingCartId;
    }

    public ProductId productId() {
        return productId;
    }

    public ProductName name() {
        return name;
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

    public Boolean isAvailable() {
        return available;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ShoppingCartItem that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    void refresh(final Product product){
        requireNonNull(product);
        this.setProductId(product.id());
        this.setName(product.name());
        this.setPrice(product.price());
        this.setAvailable(product.inStock());
        this.recalculateTotals();
    }

    void changeQuantity(final Quantity newQuantity) {
        if (requireNonNull(newQuantity).isLessThanOrEqualTo(Quantity.ZERO)){
            final String message = String.format(QUANTITY_LESS_THAN, 0);
            throw new IllegalArgumentException(message);
        }

        this.setQuantity(newQuantity);
        this.recalculateTotals();
    }

    private void recalculateTotals(){
        this.setTotalAmount(this.price.multiply(this.quantity));
    }

    private void setId(final ShoppingCartItemId id) {
        this.id = requireNonNull(id);
    }

    private void setShoppingCartId(final ShoppingCartId shoppingCartId) {
        this.shoppingCartId = requireNonNull(shoppingCartId);
    }

    private void setProductId(final ProductId productId) {
        this.productId = requireNonNull(productId);
    }

    private void setName(final ProductName name) {
        this.name = requireNonNull(name);
    }

    private void setPrice(final Money price) {
        this.price = requireNonNull(price);
    }

    private void setQuantity(final Quantity quantity) {
        if (requireNonNull(quantity).isLessThanOrEqualTo(Quantity.ZERO)){
            final String message = String.format(QUANTITY_LESS_THAN, 0);
            throw new IllegalArgumentException(message);
        }
        this.quantity = quantity;
    }

    private void setTotalAmount(final Money totalAmount) {
        this.totalAmount = requireNonNull(totalAmount);
    }

    private void setAvailable(final Boolean available) {
        this.available = requireNonNull(available);
    }
}
