package com.algaworks.algashop.ordering.utility.databuilder.entity;

import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.IdGenerator;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.embeddable.BillingEmbeddableDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.embeddable.ShippingEmbeddableDataBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class OrderPersistenceEntityDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<Long> id = () -> IdGenerator.generateTSID().toLong();
    @With
    private Supplier<CustomerPersistenceEntity> customer =
            () -> CustomerPersistenceEntityDataBuilder
                    .builder()
                    .withArchived(() -> true)
                    .build();
    @With
    private Supplier<BigDecimal> totalAmount = () -> new  BigDecimal(Double.toString(customFaker.number().randomDouble(2 ,50, 9999)));
    @With
    private Supplier<Integer> totalItems = () -> customFaker.number().numberBetween(1, 10);
    @With
    private Supplier<String> orderStatus = () -> customFaker.options().option(OrderStatus.class).toString();
    @With
    private Supplier<String> paymentMethod = () -> customFaker.options().option(PaymentMethod.class).toString();
    @With
    private Supplier<OffsetDateTime> placedAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> paidAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> canceledAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> readyAt = OffsetDateTime::now;
    @With
    private Supplier<UUID> createdBy = UUID::randomUUID;
    @With
    private Supplier<OffsetDateTime> lastModifiedAt = OffsetDateTime::now;
    @With
    private Supplier<UUID> lastModifiedBy = UUID::randomUUID;
    @With
    private Supplier<ShippingEmbeddable> shipping = () -> ShippingEmbeddableDataBuilder.builder().build();
    @With
    private Supplier<BillingEmbeddable> billing = () -> BillingEmbeddableDataBuilder.builder().build();
    @With
    private Supplier<Set<OrderItemPersistenceEntity>> items = () -> OrderItemPersistenceEntityDataBuilder.builder()
            .buildList(customFaker.number().numberBetween(1, 5));

    public static OrderPersistenceEntityDataBuilder builder() {
        return new OrderPersistenceEntityDataBuilder();
    }

    public OrderPersistenceEntity build() {
        final var genItems = this.items.get();
        final var genShipping = shipping.get();
        final var cost = Optional.ofNullable(genShipping)
                .map(ShippingEmbeddable::getCost)
                .orElse(BigDecimal.ZERO);
        final var genTotalItems = genItems.stream()
                .map(OrderItemPersistenceEntity::getQuantity)
                .reduce(0, Integer::sum);
        final var genTotalAmount = genItems.stream()
                .map(OrderItemPersistenceEntity::getTotalAmount)
                .reduce(cost, BigDecimal::add);
        return new OrderPersistenceEntity(
                id.get(),
                customer.get(),
                genTotalAmount,
                genTotalItems,
                orderStatus.get(),
                paymentMethod.get(),
                placedAt.get(),
                paidAt.get(),
                canceledAt.get(),
                readyAt.get(),
                createdBy.get(),
                lastModifiedAt.get(),
                lastModifiedBy.get(),
                null,
                billing.get(),
                genShipping,
                genItems
        );
    }

}
