package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartsPersistenceProvider;
import com.algaworks.algashop.ordering.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.DBTestContainer;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartItemDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@IntegrationTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({
        ShoppingCartsPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssemblerImpl.class,
        ShoppingCartPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class,
        DBTestContainer.class,
})
class ShoppingCartsTest extends AbstractDBTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    private final ShoppingCarts shoppingCarts;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;
    private CustomerId customerId;

    @Autowired
    ShoppingCartsTest(final JdbcTemplate jdbcTemplate,
                      final ShoppingCarts shoppingCarts,
                      final CustomerPersistenceEntityRepository customerRepository) {
        super(jdbcTemplate);
        this.shoppingCarts = shoppingCarts;
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
        this.customerEntity = CustomerPersistenceEntityDataBuilder.builder().withArchived(() -> false).build();
        this.customerEntity = customerRepository.save(customerEntity);
        customerId = new  CustomerId(customerEntity.getId());
    }

    @Test
    void shouldPersistAndFind(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(customerId))
                .build();
        shoppingCarts.add(shoppingCart);
        final var optional = shoppingCarts.ofId(shoppingCart.id());
        assertThat(optional).isPresent();
        final var actual = optional.get();
        assertThat(actual)
                .usingRecursiveComparison()
                .withComparatorForType(Comparator.comparing(OffsetDateTime::toInstant), OffsetDateTime.class)
                .isEqualTo(shoppingCart);
    }

    @Test
    void shouldUpdateExistingOrder(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(customerId))
                .build();
        shoppingCarts.add(shoppingCart);
        final var storedOrder = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        storedOrder.empty();
        shoppingCarts.add(storedOrder);
        final var actual = shoppingCarts.ofId(storedOrder.id()).orElseThrow();
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void shouldNotAllowStaleUpdates(){
        final var items = ShoppingCartItemDataBuilder
                .builder()
                .buildSet(customFaker.number().numberBetween(1, 10));
        final var order = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(customerId))
                .withItems(() -> items)
                .build();
        shoppingCarts.add(order);

        final var firstSearch = shoppingCarts.ofId(order.id()).orElseThrow();
        final var secondSearch = shoppingCarts.ofId(order.id()).orElseThrow();

        final var randomIndex = customFaker.number().numberBetween(0, items.size());
        final var randomItem = firstSearch.items().stream().toList().get(randomIndex).id();
        final var firstQuantity = customFaker.common().quantity();
        firstSearch.changeItemQuantity(randomItem, firstQuantity);
        shoppingCarts.add(firstSearch);

        var secondQuantity = customFaker.common().quantity();
        while (secondQuantity.equals(firstQuantity)) {
            secondQuantity = customFaker.common().quantity();
        }
        secondSearch.changeItemQuantity(randomItem, secondQuantity);
        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> shoppingCarts.add(secondSearch));

        final var storedOrder = shoppingCarts.ofId(order.id()).orElseThrow();
        assertThat(storedOrder.items().stream().toList().get(randomIndex).quantity()).isEqualTo(firstQuantity);
        assertThat(storedOrder.items().stream().toList().get(randomIndex).quantity()).isNotEqualTo(secondQuantity);
    }

    @Test
    void shouldReturnIfOrdersExist(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(customerId))
                .build();
        shoppingCarts.add(shoppingCart);
        assertThat(shoppingCarts.exists(shoppingCart.id())).isTrue();
    }

    @Test
    void shouldReturnIfOrdersNotExist(){
        assertThat(shoppingCarts.exists(new ShoppingCartId())).isFalse();
    }

    @Test
    void shouldFindOfCustomer(){
        final var shoppingCart = ShoppingCartDataBuilder.builder(ShoppingCart.startShopping(customerId))
                .build();
        shoppingCarts.add(shoppingCart);
        assertThat(shoppingCarts.ofCustomer(customerId)).isPresent();
    }

    @Test
    void givenCustomerWithoutShoppingCartShouldReturnEmpty(){
        assertThat(shoppingCarts.ofCustomer(customerId)).isEmpty();
    }

}
