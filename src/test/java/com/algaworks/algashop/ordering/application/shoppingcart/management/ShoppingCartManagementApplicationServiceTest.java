package com.algaworks.algashop.ordering.application.shoppingcart.management;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFound;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest
class ShoppingCartManagementApplicationServiceTest extends AbstractApplicationTest {

    private final ShoppingCarts shoppingCarts;
    private final Customers customers;
    private final ShoppingCartManagementApplicationService service;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @Autowired
    public ShoppingCartManagementApplicationServiceTest(final JdbcTemplate jdbcTemplate,
                                                        final ShoppingCarts shoppingCarts,
                                                        final Customers customers,
                                                        final ShoppingCartManagementApplicationService service) {
        super(jdbcTemplate);
        this.shoppingCarts = shoppingCarts;
        this.customers = customers;
        this.service = service;
    }

    @Test
    void shouldCreateNew(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var actual = service.createNew(customer.id().value());
        assertThat(shoppingCarts.ofId(new ShoppingCartId(actual))).isPresent();
    }

    @Test
    void givenNonStoredCustomerWhenCreateNewThenThrowException(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.createNew(customer.id().value()));
    }

    @Test
    void givenCustomerWithShoppingCartWhenCreateNewThenThrowException(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        service.createNew(customer.id().value());
        assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> service.createNew(customer.id().value()));
    }

    @Test
    void shouldAddItem(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var shoppingCartId = service.createNew(customer.id().value());
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var input = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCartId)
                .productId(product.id().value())
                .quantity(customFaker.number().positive())
                .build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
        service.addItem(input);
        final var actual = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId))
                .orElseThrow();
        assertThat(actual.items()).hasSize(1);
        assertWith(actual.items().iterator().next(),
                i -> assertThat(i.quantity()).isEqualTo(new Quantity(input.getQuantity())),
                i -> assertThat(i.name()).isEqualTo(product.name()),
                i -> assertThat(i.isAvailable()).isTrue(),
                i -> assertThat(i.price()).isEqualTo(product.price()),
                i -> assertThat(i.productId()).isEqualTo(product.id())
        );
    }

    @Test
    void givenNonStoredShoppingCartWhenAddItemThenThrowException(){
        final var input = ShoppingCartItemInput.builder()
                .shoppingCartId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .quantity(customFaker.number().positive())
                .build();
        assertThatExceptionOfType(ShoppingCartNotFound.class)
                .isThrownBy(() -> service.addItem(input));
    }

    @Test
    void givenNonStoredProductWhenAddItemThenThrowException(){
        final var input = ShoppingCartItemInput.builder()
                .shoppingCartId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .quantity(customFaker.number().positive())
                .build();
        when(productCatalogService.ofId(new ProductId(input.getProductId()))).thenReturn(Optional.empty());
        assertThatExceptionOfType(ShoppingCartNotFound.class)
                .isThrownBy(() -> service.addItem(input));
    }

    @Test
    void givenProductWithoutStockWhenAddItemThenThrowException(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var shoppingCartId = service.createNew(customer.id().value());
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> false)
                .build();
        final var input = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCartId)
                .productId(product.id().value())
                .quantity(customFaker.number().positive())
                .build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
        assertThatExceptionOfType(ProductOutOfStockException.class)
            .isThrownBy(() -> service.addItem(input));
    }

    @Test
    void shouldRemoveItem(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var shoppingCartId = service.createNew(customer.id().value());
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var input = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCartId)
                .productId(product.id().value())
                .quantity(customFaker.number().positive())
                .build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
        service.addItem(input);
        final var item = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId))
                .orElseThrow().items().iterator().next();
        service.removeItem(shoppingCartId, item.id().value());
        final var actual = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId))
                .orElseThrow();
        assertThat(actual.items()).isEmpty();
    }

    @Test
    void givenNonStoredShoppingCartWhenRemoveItemThenThrowException(){
        assertThatExceptionOfType(ShoppingCartNotFound.class)
                .isThrownBy(() -> service.removeItem(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void givenNonStoredShoppingCartItemWhenRemoveItemThenThrowException(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var shoppingCartId = service.createNew(customer.id().value());
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
        assertThatExceptionOfType(ShoppingCartDoesNotContainOrderItemException.class)
                .isThrownBy(() -> service.removeItem(shoppingCartId, UUID.randomUUID()));
    }

    @Test
    void shouldEmpty(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var shoppingCartId = service.createNew(customer.id().value());
        final var product = ProductDataBuilder.builder()
                .withInStock(() -> true)
                .build();
        final var input = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCartId)
                .productId(product.id().value())
                .quantity(customFaker.number().positive())
                .build();
        when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));
        service.addItem(input);
        service.empty(shoppingCartId);
        final var actual = shoppingCarts.ofId(new ShoppingCartId(shoppingCartId))
                .orElseThrow();
        assertThat(actual.items()).isEmpty();
    }

    @Test
    void givenNonStoredShoppingCartWhenEmptyThenThrowException(){
        assertThatExceptionOfType(ShoppingCartNotFound.class)
                .isThrownBy(() -> service.empty(UUID.randomUUID()));
    }

    @Test
    void shouldDelete(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var shoppingCartId = service.createNew(customer.id().value());
        service.delete(shoppingCartId);
        assertThat(shoppingCarts.exists(new ShoppingCartId(shoppingCartId))).isFalse();
    }

    @Test
    void givenNonStoredShoppingCartWhenDeleteThenThrowException(){
        assertThatExceptionOfType(ShoppingCartNotFound.class)
                .isThrownBy(() -> service.delete(UUID.randomUUID()));
    }

}
