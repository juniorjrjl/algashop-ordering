package com.algaworks.algashop.ordering.utility.databuilder.application;

import com.algaworks.algashop.ordering.core.port.out.order.CustomerMinimalOutput;
import com.algaworks.algashop.ordering.core.port.out.order.OrderSummaryOutput;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.core.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class OrderSummaryOutputDataBuilder {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @With
    private Supplier<Long> id = () -> new OrderId().value().toLong();
    @With
    private Supplier<Integer> totalItems = () -> customFaker.number().numberBetween(1, 10);
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
    private Supplier<CustomerMinimalOutput> customer = () -> CustomerMinimalOutputDataBuilder.builder().build();

    public static OrderSummaryOutputDataBuilder builder() {
        return new OrderSummaryOutputDataBuilder();
    }

    public OrderSummaryOutput build(){
        return new OrderSummaryOutput(
                id.get(),
                totalItems.get(),
                totalAmount.get(),
                placedAt.get(),
                paidAt.get(),
                canceledAt.get(),
                readyAt.get(),
                orderStatus.get(),
                paymentMethod.get(),
                customer.get()
        );
    }

}
