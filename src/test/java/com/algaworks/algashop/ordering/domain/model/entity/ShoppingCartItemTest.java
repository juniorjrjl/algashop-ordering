package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.ProductDataBuilder;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.ShoppingCartItemDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class ShoppingCartItemTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Test
    void shouldCreate(){
        final var shoppingCartId = new  ShoppingCartId();
        final var product = ProductDataBuilder.builder().build();
        final var quantity = customFaker.valueObject().quantity(1, 10);
        final var item = ShoppingCartItem.brandNew()
                .shoppingCartId(shoppingCartId)
                .product(product)
                .quantity(quantity)
                .build();
        assertWith(item,
                i -> assertThat(i.shoppingCartId()).isEqualTo(shoppingCartId),
                i -> assertThat(i.shoppingCartId()).isEqualTo(shoppingCartId),
                i -> assertThat(i.name()).isEqualTo(product.name()),
                i -> assertThat(i.price()).isEqualTo(product.price()),
                i -> assertThat(i.available()).isEqualTo(product.inStock()),
                i -> assertThat(i.totalAmount()).isEqualTo(product.price().multiply(quantity))
                );
    }

    @Test
    void givenShoppingCartItemWhenRefreshProductThenAllowIt(){
        final var item = ShoppingCartItemDataBuilder.builder().build();
        final var product = ProductDataBuilder.builder()
                .withId(item::productId)
                .build();
        item.refresh(product);
        assertWith(item,
                i -> assertThat(i.name()).isEqualTo(product.name()),
                i -> assertThat(i.productId()).isEqualTo(product.id()),
                i -> assertThat(i.price()).isEqualTo(product.price()),
                i -> assertThat(item.totalAmount()).isEqualTo(product.price().multiply(i.quantity()))
        );
    }

    @Test
    void givenShoppingCartItemWhenRefreshWithInvalidProductThenThrowsException(){
        final var item = ShoppingCartItemDataBuilder.builder().build();
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> item.refresh(null));
    }

    @Test
    void givenShoppingCartItemWhenChangeQuantityThenAllowIt(){
        final var item = ShoppingCartItemDataBuilder.builder().build();
        final var quantity = customFaker.valueObject().quantity();
        final var currentPrice = item.price();
        item.changeQuantity(quantity);
        assertWith(item,
                i -> assertThat(i.quantity()).isEqualTo(quantity),
                i -> assertThat(i.totalAmount()).isEqualTo(currentPrice.multiply(i.quantity()))
                );
    }

    private static final List<Arguments> givenShoppingCartItemWhenChangeQuantityWithInvalidArgsThenThrowsException
            = List.of(
                    Arguments.of(Quantity.ZERO, IllegalArgumentException.class),
                    Arguments.of(null, NullPointerException.class)
            );

    @ParameterizedTest
    @FieldSource
    void givenShoppingCartItemWhenChangeQuantityWithInvalidArgsThenThrowsException(final Quantity quantity,
                                                                                   final Class<? extends Exception> expectedException){
        final var item = ShoppingCartItemDataBuilder.builder().build();
        assertThatExceptionOfType(expectedException)
                .isThrownBy(() -> item.changeQuantity(quantity));
    }

}