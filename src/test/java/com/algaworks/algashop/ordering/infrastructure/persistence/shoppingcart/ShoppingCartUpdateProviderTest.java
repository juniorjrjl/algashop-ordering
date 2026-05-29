package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.common.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.DBTestContainer;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ShoppingCartDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.transaction.annotation.Propagation.NEVER;

@ActiveProfiles("test")
@IntegrationTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import({
        ShoppingCartUpdateProvider.class,
        ShoppingCartsPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssemblerImpl.class,
        ShoppingCartPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
        EmbeddableAssemblerImpl.class,
        DBTestContainer.class,
})
class ShoppingCartUpdateProviderTest {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    private final ShoppingCartUpdateProvider provider;
    private final ShoppingCartsPersistenceProvider shoppingCartPersistenceProvider;
    private final CustomerPersistenceEntityRepository customerRepository;
    private CustomerPersistenceEntity customerEntity;

    @Autowired
    public ShoppingCartUpdateProviderTest(final ShoppingCartUpdateProvider provider,
                                          final ShoppingCartsPersistenceProvider shoppingCartPersistenceProvider,
                                          final CustomerPersistenceEntityRepository customerRepository) {
        this.provider = provider;
        this.shoppingCartPersistenceProvider = shoppingCartPersistenceProvider;
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
        this.customerEntity = CustomerPersistenceEntityDataBuilder.builder().withArchived(() -> false).build();
        this.customerEntity = customerRepository.save(customerEntity);
    }

    @Transactional(propagation = NEVER)
    @Test
    void shouldUpdateItemPriceAndTotalAmount(){
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(this.customerEntity.getId()))
                .build();
        shoppingCartPersistenceProvider.add(shoppingCart);
        final var items = shoppingCart.items();
        final var toChangePrice = items.stream()
                .toList()
                .get(customFaker.number().numberBetween(0, items.size()));
        final var newAmount = customFaker.common().money();
        provider.adjustPrice(toChangePrice.productId(), newAmount);

        final var expectedNewTotalItem = newAmount.multiply(toChangePrice.quantity());
        final var oldTotalBigDecimal = shoppingCart.totalAmount()
                .value()
                .subtract(toChangePrice.totalAmount().value());
        final var expectedNewCartAmount = new Money(oldTotalBigDecimal).add(expectedNewTotalItem);
        final var actual = shoppingCartPersistenceProvider.ofId(shoppingCart.id())
                .orElseThrow();
        assertThat(actual.totalAmount()).isEqualTo(expectedNewCartAmount);
        final var changedItem = actual.findItem(toChangePrice.productId());
        assertThat(changedItem.price()).isEqualTo(newAmount);
        assertThat(changedItem.totalAmount()).isEqualTo(expectedNewTotalItem);
    }

    @Transactional(propagation = NEVER)
    @Test
    void shouldUpdateItemAvailability(){
        final var shoppingCart = ShoppingCartDataBuilder.builder()
                .withCustomerId(() -> new CustomerId(this.customerEntity.getId()))
                .build();
        shoppingCartPersistenceProvider.add(shoppingCart);
        final var items = shoppingCart.items();
        final var toChangeAvailability = items.stream()
                .toList()
                .get(customFaker.number().numberBetween(0, items.size()));
        provider.changeAvailability(toChangeAvailability.productId(), !toChangeAvailability.isAvailable());

        final var actual = shoppingCartPersistenceProvider.ofId(shoppingCart.id())
                .orElseThrow();
        final var changedItem = actual.findItem(toChangeAvailability.productId());
        assertThat(changedItem.isAvailable()).isEqualTo(!toChangeAvailability.isAvailable());
    }

}