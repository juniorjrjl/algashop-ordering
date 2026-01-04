package com.algaworks.algashop.ordering.domain.model.utility.databuilder.entity;

import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;
import com.algaworks.algashop.ordering.infrastruct.persistence.entity.OrderPersistenceEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
    private Supplier<UUID> customerId = IdGenerator::generateUUID;
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

    public static OrderPersistenceEntityDataBuilder builder() {
        return new OrderPersistenceEntityDataBuilder();
    }

    public OrderPersistenceEntity build() {
        return new OrderPersistenceEntity(
                id.get(),
                customerId.get(),
                totalAmount.get(),
                totalItems.get(),
                orderStatus.get(),
                paymentMethod.get(),
                placedAt.get(),
                paidAt.get(),
                canceledAt.get(),
                readyAt.get()
        );
    }

}
