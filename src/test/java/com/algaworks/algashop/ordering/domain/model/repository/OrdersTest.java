package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.utility.AbstractDBTest;
import com.algaworks.algashop.ordering.domain.model.utility.databuilder.domain.OrderDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.EmbeddableDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassemblerImpl;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.algaworks.algashop.ordering.domain.model.entity.OrderStatus.PLACED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceEntityAssemblerImpl.class,
        OrderPersistenceEntityDisassemblerImpl.class,
        EmbeddableDisassemblerImpl.class,
})
class OrdersTest extends AbstractDBTest {

    private final Orders orders;

    @Autowired
    OrdersTest(final Orders orders, final JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.orders = orders;
    }

    @Test
    void shouldPersistAndFind(){
        final var order = OrderDataBuilder.builder(Order.draft(new CustomerId()))
                .buildExisting();
        orders.add(order);
        final var optional = orders.ofId(order.id());
        assertThat(optional).isPresent();
        final var actual = optional.get();
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(order);
    }

    @Test
    void shouldUpdateExistingOrder(){
        final var order = OrderDataBuilder.builder()
                .withOrderStatus(() -> PLACED)
                .buildExisting();
        orders.add(order);
        final var storedOrder = orders.ofId(order.id()).orElseThrow();
        storedOrder.markAsPaid();
        orders.add(storedOrder);
        final var actual = orders.ofId(storedOrder.id()).orElseThrow();
        assertThat(actual.isPaid()).isTrue();
    }

    @Test
    void shouldNotAllowStaleUpdates(){
        final var order = OrderDataBuilder.builder()
                .withOrderStatus(() -> PLACED)
                .withPaidAt(() -> null)
                .withCanceledAt(() -> null)
                .buildExisting();
        orders.add(order);

        final var firstSearch = orders.ofId(order.id()).orElseThrow();
        final var secondSearch = orders.ofId(order.id()).orElseThrow();

        firstSearch.markAsPaid();
        orders.add(firstSearch);

        secondSearch.cancel();
        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> orders.add(secondSearch));

        final var storedOrder = orders.ofId(order.id()).orElseThrow();
        assertThat(storedOrder.canceledAt()).isNull();
        assertThat(storedOrder.paidAt()).isNotNull();
    }

    @Test
    void shouldCountExistingOrders(){
        assertThat(orders.count()).isZero();
        final var toInsert = Stream.generate(() -> OrderDataBuilder.builder().buildExisting())
                .limit(customFaker.number().numberBetween(1, 10))
                .collect(Collectors.toSet());
        toInsert.forEach(orders::add);
        assertThat(orders.count()).isEqualTo(toInsert.size());
    }

    @Test
    void shouldReturnIfOrdersExist(){
        final var order = OrderDataBuilder.builder().buildExisting();
        orders.add(order);
        assertThat(orders.exists(order.id())).isTrue();
    }

    @Test
    void shouldReturnIfOrdersNotExist(){
        assertThat(orders.exists(new OrderId())).isFalse();
    }

}
