package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.OrderItemDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.order.OrderStatus.READY;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class CustomerLoyaltyPointsServiceTest {

    private final CustomFaker customFaker = CustomFaker.getInstance();

    private CustomerLoyaltyPointsService service;

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
        service = new CustomerLoyaltyPointsService();
    }

    @Test
    void givenValidCustomerAndOrderWhenAddingPointsShouldAccumulate(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        final var items = OrderItemDataBuilder.builder()
                .withProduct(() -> ProductDataBuilder.builder()
                        .withInStock(() -> true)
                        .withPrice(() -> customFaker.common().money(1000, 9999))
                        .build())
                .buildExistingList(customFaker.number().numberBetween(1, 9));
        final var shipping = customFaker.order()
                .shipping()
                .toBuilder()
                .cost(Money.ZERO)
                .build();
        final var order = OrderDataBuilder.builder()
                .withCustomerId(customer::id)
                .withOrderStatus(() -> READY)
                .withItems(() -> items)
                .withShipping(() -> shipping)
                .buildExisting();

        service.addPoints(customer, order);

        final var amount = order.totalAmount().value();
        final var expectedLoyaltyPoints = new LoyaltyPoints((amount.intValue() / 1000) * 5);
        assertThat(customer.loyaltyPoints()).isEqualTo(expectedLoyaltyPoints);
    }

    @Test
    void givenValidCustomerAndOrderWithLowTotalAmountWhenAddingPointsShouldNotAccumulate(){
        final var customer = CustomerDataBuilder.builder().buildNew();
        final var items = OrderItemDataBuilder.builder()
                .withProduct(() -> ProductDataBuilder.builder()
                        .withInStock(() -> true)
                        .withPrice(() -> customFaker.common().money(1, 100))
                        .build()).buildExistingList(customFaker.number().numberBetween(1, 3));
        final var order = OrderDataBuilder.builder()
                .withCustomerId(customer::id)
                .withOrderStatus(() -> READY)
                .withItems(() -> items)
                .buildExisting();

        service.addPoints(customer, order);

        assertThat(customer.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }

}