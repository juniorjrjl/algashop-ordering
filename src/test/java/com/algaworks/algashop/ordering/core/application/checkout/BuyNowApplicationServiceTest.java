package com.algaworks.algashop.ordering.core.application.checkout;

import com.algaworks.algashop.ordering.core.application.checkout.BuyNowApplicationService;
import com.algaworks.algashop.ordering.core.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.domain.model.order.OriginAddressService;
import com.algaworks.algashop.ordering.core.domain.model.order.ShippingCostService;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.utility.CustomFaker;
import com.algaworks.algashop.ordering.utility.databuilder.application.BuyNowInputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.CustomerDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.domain.ProductDataBuilder;
import com.algaworks.algashop.ordering.utility.extension.PGContainer;
import com.algaworks.algashop.ordering.utility.extension.PostgreSQLTestContainerExtension;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@IntegrationTest
@SpringBootTest
@ExtendWith(PostgreSQLTestContainerExtension.class)
class BuyNowApplicationServiceTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @MockitoBean
    private OriginAddressService originAddressService;
    @MockitoBean
    private ProductCatalogService productCatalogService;
    @MockitoBean
    private ShippingCostService shippingCostService;

    @PGContainer
    private static PostgreSQLContainer postgreSQLContainer;

    private Customer customer;

    private final BuyNowApplicationService service;
    private final Orders orders;
    private final Customers customers;

    @Autowired
    public BuyNowApplicationServiceTest(final BuyNowApplicationService service,
                                        final Orders orders,
                                        final Customers customers) {
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

    @DynamicPropertySource
    public static void configurePropertySource(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
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