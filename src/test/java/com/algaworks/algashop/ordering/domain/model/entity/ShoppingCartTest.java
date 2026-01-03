package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.DomainException;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.ProductDataBuilder;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.ShoppingCartDataBuilder;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.ShoppingCartItemDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class ShoppingCartTest {

    private static final CustomFaker customFaker = new CustomFaker();

    @Test
    void givenValidProductsAndQuantitiesWhenAddItemShouldAllowIt(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(new CustomerId()))
                .build();
        final var product1 = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var quantity1 = customFaker.valueObject().quantity(1, 10);
        shoppingCart.addItem(
                product1,
                quantity1
        );

        final var product2 = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var quantity2 = customFaker.valueObject().quantity(1, 10);
        shoppingCart.addItem(
                product2,
                quantity2
        );

        final var totalProduct1 = product1.price().multiply(quantity1);
        final var totalProduct2 = product2.price().multiply(quantity2);
        assertWith(shoppingCart,
                s -> assertThat(s.items()).hasSize(2),
                s -> assertThat(s.totalAmount()).isEqualTo(totalProduct1.add(totalProduct2)),
                s -> assertThat(s.totalItems()).isEqualTo(quantity1.add(quantity2))
                );
    }

    @Test
    void givenSameProductTwiceWhenAddItemShouldAllowIt(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(new CustomerId()))
                .build();
        final var product = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var quantity1 = customFaker.valueObject().quantity(1, 10);
        shoppingCart.addItem(
                product,
                quantity1
        );

        final var quantity2 = customFaker.valueObject().quantity(1, 10);
        shoppingCart.addItem(
                product,
                quantity2
        );

        final var totalProduct1 = product.price().multiply(quantity1);
        final var totalProduct2 = product.price().multiply(quantity2);
        assertWith(shoppingCart,
                s -> assertThat(s.items()).hasSize(1),
                s -> assertThat(s.totalAmount()).isEqualTo(totalProduct1.add(totalProduct2)),
                s -> assertThat(s.totalItems()).isEqualTo(quantity1.add(quantity2))
        );
    }

    private static final List<Arguments> givenShoppingCartWhenAddItemWithInvalidArgsShouldThrowException
            = List.of(
                    Arguments.of(null, customFaker.valueObject().quantity(1, 10)),
                    Arguments.of(ProductDataBuilder.builder().withInStock(() -> true).build(), null)
            );

    @ParameterizedTest
    @FieldSource
    void givenShoppingCartWhenAddItemWithInvalidArgsShouldThrowException(final Product product, final Quantity quantity){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(new CustomerId()))
                .build();
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> shoppingCart.addItem(product, quantity));
    }

    @ParameterizedTest
    @ArgumentsSource(ShoppingCartNotFoundOperationProvider.class)
    void givenShoppingCartWhenExecuteOperationForNonExistingItemShouldThrowException(final Consumer<ShoppingCart> tryOperation,
                                                                                     final Class<? extends DomainException> expectedException){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(new CustomerId()))
                .build();
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> tryOperation.accept(shoppingCart));
    }

    @Test
    void givenShoppingCartWhenRemoveItemShouldAllowIt(){
        final var shoppingCart = ShoppingCartDataBuilder.builder().build();
        final var itemsAmount = shoppingCart.items().size();
        final var totalItems =  shoppingCart.totalItems();
        final var totalAmount = shoppingCart.totalAmount();
        final var randomItem = shoppingCart.items()
                .stream()
                .toList()
                .get(customFaker.number().numberBetween(0, shoppingCart.items().size()));
        shoppingCart.removeItem(randomItem.id());
        final var expectedTotalItems = new Quantity(totalItems.value() - randomItem.quantity().value());
        final var expectedTotalAmount = new Money(totalAmount.value().subtract(randomItem.totalAmount().value()));
        assertWith(shoppingCart,
                s -> assertThat(s.items()).hasSize(itemsAmount - 1),
                s -> assertThat(s.items()).isNotNull(),
                s -> assertThat(s.items()).doesNotContain(randomItem),
                s -> assertThat(s.totalItems()).isEqualTo(expectedTotalItems),
                s -> assertThat(s.totalAmount()).isEqualTo(expectedTotalAmount)
        );
    }

    @Test
    void givenShoppingCartWhenRemoveNonExistingItemShouldThrowException(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(new CustomerId()))
                .build();
        assertThatExceptionOfType(ShoppingCartDoesNotContainOrderItemException.class)
                .isThrownBy(() -> shoppingCart.removeItem(new ShoppingCartItemId()));
    }

    @Test
    void givenShoppingCartWhenRefreshSomeItemShouldAllowIt(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(new CustomerId()))
                .build();
        final var product = ProductDataBuilder.builder().build();
        shoppingCart.addItem(product, customFaker.valueObject().quantity(1, 10));
        final var refreshedProduct = ProductDataBuilder.builder()
                .withId(product::id)
                .build();
        shoppingCart.refreshItem(refreshedProduct);
        final var expectedAmount = refreshedProduct.price().multiply(shoppingCart.totalItems());
        final var actualItem = shoppingCart.items().iterator().next();
        assertWith(shoppingCart,
                s -> assertThat(s.items()).hasSize(1),
                s -> assertThat(s.totalAmount()).isEqualTo(expectedAmount)
        );
        assertWith(actualItem,
                i -> assertThat(i.name()).isEqualTo(refreshedProduct.name()),
                i -> assertThat(i.productId()).isEqualTo(refreshedProduct.id()),
                i -> assertThat(i.price()).isEqualTo(refreshedProduct.price())
                );

    }

    @Test
    void givenShoppingCartWhenChangeQuantityShouldAllowIt(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(new CustomerId()))
                .build();
        final var product = ProductDataBuilder.builder().build();
        shoppingCart.addItem(product, customFaker.valueObject().quantity(1, 10));
        final var item = shoppingCart.items().iterator().next();
        final var quantity = customFaker.valueObject().quantity(1, 10);
        shoppingCart.changeItemQuantity(item.id(), quantity);

        final var expectedAmount = item.price().multiply(quantity);
        assertWith(shoppingCart,
                s -> assertThat(s.totalAmount()).isEqualTo(expectedAmount),
                s -> assertThat(s.totalItems()).isEqualTo(quantity)
                );
    }

    private static final List<Arguments> giverShoppingCartWhenChangeQuantityWithInvalidArgsShouldThrowException
            = List.of(
                    Arguments.of(null, customFaker.valueObject().quantity(1, 10)),
                    Arguments.of(new ShoppingCartItemId(), null)
            );

    @ParameterizedTest
    @FieldSource
    void giverShoppingCartWhenChangeQuantityWithInvalidArgsShouldThrowException(final ShoppingCartItemId itemId, final Quantity quantity){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(new CustomerId()))
                .build();
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> shoppingCart.changeItemQuantity(itemId, quantity));
    }

    @Test
    void giveShoppingCartWhenEmptyShouldAllowIt(){
        final var shoppingCart = ShoppingCartDataBuilder.builder().build();
        assertThat(shoppingCart.isEmpty()).isFalse();
        shoppingCart.empty();
        assertThat(shoppingCart.isEmpty()).isTrue();
    }

    private static Stream<Runnable> givenShoppingCartWhenCheckUnavailableItemsShouldAllowIt() {
        final var unavailableItem = ShoppingCartItemDataBuilder.builder()
                .withAvailable(() -> false)
                .build();
        final var availableItems = ShoppingCartItemDataBuilder.builder()
                .withAvailable(() -> true)
                .buildSet(customFaker.number().numberBetween(2, 10));
        final var allItems = new HashSet<>(availableItems);
        allItems.add(unavailableItem);
        final var withoutUnavailableItems = ShoppingCartDataBuilder.builder().withItems(() -> availableItems).build();
        final var withUnavailableItems =ShoppingCartDataBuilder.builder().withItems(() -> allItems).build();
        return Stream.of(
                () -> assertThat(withoutUnavailableItems.containsUnavailable()).isFalse(),
                () -> assertThat(withUnavailableItems.containsUnavailable()).isTrue()
        );
    }

    @ParameterizedTest
    @MethodSource
    void givenShoppingCartWhenCheckUnavailableItemsShouldAllowIt(final Runnable assertion){
        assertion.run();
    }

}