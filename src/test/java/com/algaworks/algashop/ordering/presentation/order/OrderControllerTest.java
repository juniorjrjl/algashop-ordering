package com.algaworks.algashop.ordering.presentation.order;

import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utility.AlgaShopResourceUtils;
import com.algaworks.algashop.ordering.utility.databuilder.entity.CustomerPersistenceEntityDataBuilder;
import com.algaworks.algashop.ordering.utility.tag.IntegrationTest;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.path.json.config.JsonPathConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.contract.spec.internal.HttpStatus.BAD_GATEWAY;
import static org.springframework.cloud.contract.spec.internal.HttpStatus.CREATED;
import static org.springframework.cloud.contract.spec.internal.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.cloud.contract.spec.internal.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@IntegrationTest
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "algashop.integrations.shipping.provider=FAKE"
        })
class OrderControllerTest {

    private static boolean databaseInitialized;

    private final UUID customerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @Autowired
    private OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    private WireMockServer wireMockProductCatalog;
    private WireMockServer wireMockRapidex;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        RestAssured.config().jsonConfig(JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        initDatabase();

        wireMockProductCatalog = new WireMockServer(
                options().port(8781)
                        .usingFilesUnderDirectory("src/test/resources/wiremock/product-catalog")
                        .globalTemplating(true)
        );
        wireMockRapidex = new WireMockServer(
                options().port(8780)
                        .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex")
                        .globalTemplating(true)
        );
        wireMockProductCatalog.start();
        wireMockRapidex.start();
    }

    @AfterEach
    void tearDown() {
        wireMockProductCatalog.stop();
        wireMockRapidex.stop();
    }

    private void initDatabase(){
        if(databaseInitialized){
            return;
        }
        final var customer = CustomerPersistenceEntityDataBuilder.builder()
                .withId(() -> customerId)
                .withBirthDate(() -> LocalDate.now().minusYears(20))
                .build();
        customerPersistenceEntityRepository.saveAndFlush(customer);
        databaseInitialized = true;
    }

    @Test
    void shouldNotCreateOrderUsingProductWhenCustomerWasNotFound() {
        final var json = AlgaShopResourceUtils.readContent("json/create-order-with-product-and-invalid-customer.json");
        RestAssured
            .given()
                .accept(APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(UNPROCESSABLE_ENTITY);

    }

    @Test
    void shouldCreateOrderUsingProduct(){
        final var json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");
        final var createdOrderId = RestAssured
            .given()
                .accept(APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(APPLICATION_JSON_VALUE)
                .statusCode(CREATED)
                .body("id", Matchers.not(Matchers.emptyString()),
                        "customer.id", Matchers.is(customerId.toString()))
                .extract()
                .jsonPath().getString("id");

        final var orderExists = orderPersistenceEntityRepository
                .existsById(new OrderId(createdOrderId).value().toLong());
        assertThat(orderExists).isTrue();

    }

    @Test
    void shouldNotCreateOrderUsingProductWhenProductAPIIsUnavailable() {
        final var json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");
        wireMockProductCatalog.stop();
        RestAssured
            .given()
                .accept(APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(GATEWAY_TIMEOUT);
    }

    @Test
    void shouldNotCreateOrderUsingProductWhenProductNotExists() {
        final var json = AlgaShopResourceUtils.readContent("json/create-order-with-invalid-product.json");
        RestAssured
            .given()
                .accept(APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(BAD_GATEWAY);
    }

}
