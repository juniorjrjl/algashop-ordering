package com.algaworks.algashop.ordering.core.application.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerAlreadyHaveShoppingCartException;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.infrastructure.adapter.in.listener.shoppingcart.ShoppingCartEventListener;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
class ShoppingCartManagementApplicationServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final ShoppingCarts shoppingCarts;
    private final Customers customers;
    private final ShoppingCartManagementApplicationService service;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoSpyBean
    private ShoppingCartEventListener eventListener;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    @Autowired
    public ShoppingCartManagementApplicationServiceTest(final ShoppingCarts shoppingCarts,
                                                        final Customers customers,
                                                        final ShoppingCartManagementApplicationService service) {
        this.shoppingCarts = shoppingCarts;
        this.customers = customers;
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        CustomFaker.getInstance().reseed();
    }

    @DynamicPropertySource
    public static void configurePropertySource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @Test
    void shouldCreateNew(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        final var actual = service.createNew(customer.id().value());
        assertThat(shoppingCarts.ofId(new ShoppingCartId(actual))).isPresent();
        verify(eventListener).listener(any(ShoppingCartCreatedEvent.class));
    }

    @Test
    void givenNonStoredCustomerWhenCreateNewThenThrowException(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.createNew(customer.id().value()));
        verifyNoInteractions(eventListener);
    }

    @Test
    void givenCustomerWithShoppingCartWhenCreateNewThenThrowException(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
        service.createNew(customer.id().value());
        assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> service.createNew(customer.id().value()));
        verify(eventListener, times(1)).listener(any(ShoppingCartCreatedEvent.class));
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
        verify(eventListener).listener(any(ShoppingCartItemAddedEvent.class));
    }

    @Test
    void givenNonStoredShoppingCartWhenAddItemThenThrowException(){
        final var input = ShoppingCartItemInput.builder()
                .shoppingCartId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .quantity(customFaker.number().positive())
                .build();
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
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
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
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
        verify(eventListener, never()).listener(any(ShoppingCartItemAddedEvent.class));
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
        verify(eventListener).listener(any(ShoppingCartItemRemovedEvent.class));
    }

    @Test
    void givenNonStoredShoppingCartWhenRemoveItemThenThrowException(){
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.removeItem(UUID.randomUUID(), UUID.randomUUID()));
        verifyNoInteractions(eventListener);
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
        verify(eventListener, never()).listener(any(ShoppingCartItemRemovedEvent.class));
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
        verify(eventListener).listener(any(ShoppingCartEmptiedEvent.class));
    }

    @Test
    void givenNonStoredShoppingCartWhenEmptyThenThrowException(){
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
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
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> service.delete(UUID.randomUUID()));
    }

}
