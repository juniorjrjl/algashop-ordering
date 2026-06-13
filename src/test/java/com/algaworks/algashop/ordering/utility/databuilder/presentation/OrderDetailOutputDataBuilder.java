package com.algaworks.algashop.ordering.utility.databuilder.presentation;

import com.algaworks.algashop.ordering.core.port.in.order.BillingData;
import com.algaworks.algashop.ordering.core.port.out.order.CustomerMinimalOutput;
import com.algaworks.algashop.ordering.core.port.out.order.OrderDetailOutput;
import com.algaworks.algashop.ordering.core.port.out.order.OrderItemDataDetailOutput;
import com.algaworks.algashop.ordering.core.port.in.order.ShippingData;
import com.algaworks.algashop.ordering.core.domain.model.IdGenerator;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.core.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class OrderDetailOutputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<String> id = () -> IdGenerator.generateTSID().toString();
    @With
    private Supplier<CustomerMinimalOutput> customer = () -> CustomerMinimalOutputDataBuilder.builder().build();
    @With
    private Supplier<Integer> totalItems = () -> customFaker.number().positive();
    @With
    private Supplier<BigDecimal> totalAmount = () -> customFaker.numeric().nonZeroPositive();
    @With
    private Supplier<OffsetDateTime> placedAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> paidAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> canceledAt = OffsetDateTime::now;
    @With
    private Supplier<OffsetDateTime> readyAt = OffsetDateTime::now;
    @With
    private Supplier<String> orderStatus = () -> customFaker.options().option(OrderStatus.class).toString();
    @With
    private Supplier<String> paymentMethod = () -> customFaker.options().option(PaymentMethod.class).toString();
    @With
    private Supplier<UUID> creditCardId = UUID::randomUUID;
    @With
    private Supplier<ShippingData> shipping = () -> ShippingDataDataBuilder.builder().build();
    @With
    private Supplier<BillingData> billing = () -> BillingDataDataBuilder.builder().build();
    @With
    private Supplier<List<OrderItemDataDetailOutput>> items = () -> OrderItemDataDetailOutputDataBuilder.builder()
            .buildList(customFaker.number().numberBetween(2, 5));

    public static OrderDetailOutputDataBuilder builder(){
        return new OrderDetailOutputDataBuilder();
    }

    public OrderDetailOutput build(){
        return new OrderDetailOutput(
                id.get(),
                customer.get(),
                totalItems.get(),
                totalAmount.get(),
                placedAt.get(),
                paidAt.get(),
                canceledAt.get(),
                readyAt.get(),
                orderStatus.get(),
                paymentMethod.get(),
                creditCardId.get(),
                shipping.get(),
                billing.get(),
                items.get()
        );
    }
}
