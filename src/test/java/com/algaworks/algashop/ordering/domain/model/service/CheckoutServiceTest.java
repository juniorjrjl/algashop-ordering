package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.BillingDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartItemDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertWith;

@UnitTest
class CheckoutServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private CheckoutService service;

    @BeforeEach
    void setUp() {
        service = new CheckoutService();
    }

    @Test
    void givenValidArgsWhenCheckoutThenReturnOrder(){
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withItems(() -> ShoppingCartItemDataBuilder.builder()
                        .withAvailable(() -> true)
                        .buildSet(customFaker.number().numberBetween(1, 1)))
                .build();
        final var expectedCartAmount = shoppingCart.totalAmount();
        final var expectedQuantity = shoppingCart.totalItems();
        final var billing = BillingDataBuilder.builder().build();
        final var shipping = customFaker.valueObject().shipping();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        final var actual = service.checkout(shoppingCart, billing, shipping, paymentMethod);
        final var expectedOrderAmount = expectedCartAmount.add(actual.shipping().cost());

        assertWith(actual,
                o -> assertThat(o.customerId()).isEqualTo(shoppingCart.customerId()),
                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.totalAmount()).isEqualTo(expectedOrderAmount),
                o -> assertThat(o.totalItems()).isEqualTo(expectedQuantity),
                o -> assertThat(o.isPlaced()).isTrue(),
                o -> assertThat(o.placedAt()).isNotNull(),
                o -> assertThat(o.paidAt()).isNull(),
                o -> assertThat(o.canceledAt()).isNull(),
                o -> assertThat(o.readyAt()).isNull()
                );
        final var actualItems = shoppingCart.items();
        while (actualItems.iterator().hasNext()){
            final var actualItem = actualItems.iterator().next();
            final var shoppingCartItem = shoppingCart.findItem(actualItem.productId());
            assertWith(actualItem,
                    i -> assertThat(i.name()).isEqualTo(shoppingCartItem.name()),
                    i -> assertThat(i.price()).isEqualTo(shoppingCartItem.price()),
                    i -> assertThat(i.isAvailable()).isEqualTo(shoppingCartItem.isAvailable())
                    );
        }
        assertWith(shoppingCart,
                c -> assertThat(c.totalAmount()).isEqualTo(Money.ZERO),
                c -> assertThat(c.totalItems()).isEqualTo(Quantity.ZERO),
                c -> assertThat(c.isEmpty()).isTrue()
                );
    }

    private static final List<Arguments> givenNullArgWhenCheckoutThenThrowException = List.of(
            Arguments.of(
                    null,
                    BillingDataBuilder.builder().build(),
                    customFaker.valueObject().shipping(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ShoppingCartDataBuilder.builder().build(),
                    null,
                    customFaker.valueObject().shipping(),
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ShoppingCartDataBuilder.builder().build(),
                    BillingDataBuilder.builder().build(),
                    null,
                    customFaker.options().option(PaymentMethod.class)
            ),
            Arguments.of(
                    ShoppingCartDataBuilder.builder().build(),
                    BillingDataBuilder.builder().build(),
                    customFaker.valueObject().shipping(),
                    null
            )
    );

    @FieldSource
    @ParameterizedTest
    void givenNullArgWhenCheckoutThenThrowException(final ShoppingCart shoppingCart,
                                                    final Billing billing,
                                                    final Shipping shipping,
                                                    final PaymentMethod paymentMethod) {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> service.checkout(null, billing, shipping, paymentMethod));
    }

    private static Stream<ShoppingCart> givenUnprocessableShoppingCartWhenCheckoutThenThrowException(){
        final var emptyShoppingCart = ShoppingCartDataBuilder.builder()
                .withItems(HashSet::new)
                .build();
        final var availableItems = ShoppingCartItemDataBuilder.builder()
                .withAvailable(() -> true)
                .buildSet(3);
        final var unavailableItem = ShoppingCartItemDataBuilder.builder()
                .withAvailable(() -> false)
                .build();
        availableItems.add(unavailableItem);
        final var unavaliableItemShoppingCart = ShoppingCartDataBuilder.builder()
                .withItems(() -> availableItems)
                .build();
        return Stream.of(emptyShoppingCart, unavaliableItemShoppingCart);
    }

    @MethodSource
    @ParameterizedTest
    void givenUnprocessableShoppingCartWhenCheckoutThenThrowException(final ShoppingCart shoppingCart){
        final var billing = BillingDataBuilder.builder().build();
        final var shipping = customFaker.valueObject().shipping();
        final var paymentMethod = customFaker.options().option(PaymentMethod.class);
        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> service.checkout(shoppingCart, billing, shipping, paymentMethod));
    }

}