package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartDoesNotContainProductException;
import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.ProductDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class ShoppingCartNotFoundOperationProvider  implements ArgumentsProvider {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @Override
    @NonNull
    public Stream<? extends Arguments> provideArguments(
            @NonNull final ParameterDeclarations parameters,
            @NonNull final ExtensionContext context) {
        final Consumer<ShoppingCart> tryRemove = s -> s.removeItem(new ShoppingCartItemId());
        final Consumer<ShoppingCart> tryRefresh = s -> s.refreshItem(ProductDataBuilder.builder().build());
        final Consumer<ShoppingCart> tryChangeQuantity = s -> s.changeItemQuantity(
                new ShoppingCartItemId(),
                customFaker.valueObject().quantity()
        );
        return Stream.of(
                Arguments.of(tryRemove, ShoppingCartDoesNotContainOrderItemException.class),
                Arguments.of(tryRefresh, ShoppingCartDoesNotContainProductException.class),
                Arguments.of(tryChangeQuantity, ShoppingCartDoesNotContainOrderItemException.class)
        );
    }

}
