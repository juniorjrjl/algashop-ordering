package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.utility.AbstractApplicationTest;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.application.BuyNowInputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class BuyNowApplicationServiceTest extends AbstractApplicationTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @MockitoBean
    private OriginAddressService originAddressService;
    @MockitoBean
    private ProductCatalogService productCatalogService;
    @MockitoBean
    private ShippingCostService shippingCostService;

    private Customer customer;

    private final BuyNowApplicationService service;
    private final Orders orders;
    private final Customers customers;

    @Autowired
    public BuyNowApplicationServiceTest(final JdbcTemplate jdbcTemplate,
                                        final BuyNowApplicationService service,
                                        final Orders orders,
                                        final Customers customers) {
        super(jdbcTemplate);
        this.service = service;
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
        customer = CustomerDataBuilder.builder().buildNew();
        customers.add(customer);
    }

    @Test
    void shouldBuyNow(){
        final var product = ProductDataBuilder.builder().withInStock(() -> true).build();
        final var input = BuyNowInputDataBuilder.builder()
                .withCustomerId(() -> customer.id().value())
                .withProductId(() -> product.id().value())
                .build();
        when(productCatalogService.ofId(new ProductId(input.getProductId()))).thenReturn(Optional.of(product));

        final var originAddress = customFaker.common().address();
        when(originAddressService.originAddress()).thenReturn(originAddress);
        final var calculationResult = new ShippingCostService.CalculationResult(
                customFaker.common().money(),
                LocalDate.ofInstant(customFaker.timeAndDate().future(), UTC)
        );
        final var calculationRequest = new ShippingCostService.CalculationRequest(
                originAddress.zipCode(),
                new ZipCode(input.getShipping().getAddress().getZipCode())
        );
        when(shippingCostService.calculate(calculationRequest)).thenReturn(calculationResult);

        final var actual = service.buyNow(input);
        assertThat(actual).isNotBlank();
        assertThat(orders.exists(new OrderId(actual))).isTrue();
    }

}