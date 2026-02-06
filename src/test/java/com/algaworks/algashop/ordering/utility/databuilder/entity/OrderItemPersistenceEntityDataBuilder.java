package com.algaworks.algashop.ordering.utility.databuilder.entity;

import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.domain.model.IdGenerator;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderItemPersistenceEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class OrderItemPersistenceEntityDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<Long> id = () -> customFaker.number().randomNumber();
    private Supplier<UUID> productId = IdGenerator::generateUUID;
    private Supplier<String> productName = () -> customFaker.lorem().word();
    private Supplier<BigDecimal> price =
            () -> new BigDecimal(Double.toString(customFaker.number().randomDouble(2 ,1, 100)));
    private Supplier<Integer> quantity = () -> customFaker.number().randomDigit();
    private Supplier<BigDecimal> totalAmount =
            () -> new BigDecimal(Double.toString(customFaker.number().randomDouble(2 ,1, 100)));

    public static OrderItemPersistenceEntityDataBuilder builder() {
        return new OrderItemPersistenceEntityDataBuilder();
    }

    public OrderItemPersistenceEntity build() {
        return new OrderItemPersistenceEntity(
                id.get(),
                productId.get(),
                productName.get(),
                price.get(),
                quantity.get(),
                totalAmount.get(),
                null
        );
    }

    public Set<OrderItemPersistenceEntity> buildList(final int amount){
        return Stream.generate(this::build)
                .limit(amount).collect(Collectors.toSet());
    }

}
