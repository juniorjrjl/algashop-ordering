package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.application.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerEmailInUseException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerOutputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.application.CustomerSummaryOutputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.presentation.CustomerInputJsonDataBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final WebApplicationContext context;

    @MockitoBean
    private CustomerManagementApplicationService applicationService;
    @MockitoBean
    private CustomerQueryService queryService;

    @Autowired
    CustomerControllerTest(final WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(UTF_8)
                .build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void createCustomerContract(){
        final var customerId = UUID.randomUUID();
        when(applicationService.create(any(CustomerInput.class))).thenReturn(customerId);
        final var output = CustomerOutputDataBuilder.builder().build();
        final var addressOutput = output.getAddress();
        when(queryService.findById(any(UUID.class))).thenReturn(output);
        final var jsonInput = CustomerInputJsonDataBuilder.builder().build();
        RestAssuredMockMvc
            .given()
                .accept(APPLICATION_JSON_VALUE)
                .body(jsonInput.toString())
                .contentType(APPLICATION_JSON_VALUE)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(APPLICATION_JSON_VALUE)
                .status(CREATED)
                .header("Location", Matchers.containsString("/api/v1/customers/" + customerId))
                .body(
                        "id", Matchers.is(output.getId().toString()),
                        "registeredAt", Matchers.is(output.getRegisteredAt().toString()),
                        "firstName", Matchers.is(output.getFirstName()),
                        "lastName", Matchers.is(output.getLastName()),
                        "email", Matchers.is(output.getEmail()),
                        "document", Matchers.is(output.getDocument()),
                        "phone", Matchers.is(output.getPhone()),
                        "birthDate", Matchers.is(output.getBirthDate().toString()),
                        "promotionNotificationsAllowed", Matchers.is(output.isPromotionNotificationsAllowed()),
                        "loyaltyPoints", Matchers.is(output.getLoyaltyPoints()),
                        "address.street", Matchers.is(addressOutput.getStreet()),
                        "address.number", Matchers.is(addressOutput.getNumber()),
                        "address.complement", Matchers.is(addressOutput.getComplement()),
                        "address.neighborhood", Matchers.is(addressOutput.getNeighborhood()),
                        "address.city", Matchers.is(addressOutput.getCity()),
                        "address.state", Matchers.is(addressOutput.getState()),
                        "address.zipCode", Matchers.is(addressOutput.getZipCode())
                );
    }

    @Test
    void createCustomerErrorRequestContract() {
        final var jsonInput = CustomerInputJsonDataBuilder.builder()
                .withFirstName(() -> null)
                .withLastName(() -> null)
                .build();
        RestAssuredMockMvc
                .given()
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .body(jsonInput.toString())
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .status(BAD_REQUEST)
                .body(
                        "status", Matchers.is(BAD_REQUEST.value()),
                        "type", Matchers.is("/errors/invalid-fields"),
                        "title", Matchers.notNullValue(),
                        "detail", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue(),
                        "fields", Matchers.notNullValue()
                );
    }

    private static final List<Arguments> createCustomerErrorContract =
            List.of(
                    Arguments.of(
                            new CustomerEmailInUseException(),
                            CONFLICT,
                            "conflict"
                    ),
                    Arguments.of(
                            new DomainException(),
                            UNPROCESSABLE_CONTENT,
                            "unprocessable-entity"
                    )
            );

    @ParameterizedTest
    @FieldSource
    void createCustomerErrorContract(final Throwable throwable,
                                     final HttpStatus expectedStatus,
                                     final String expectedType) {
        when(applicationService.create(any(CustomerInput.class))).thenThrow(throwable);
        final var jsonInput = CustomerInputJsonDataBuilder.builder()
                .build();
        RestAssuredMockMvc
            .given()
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .body(jsonInput.toString())
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .status(expectedStatus)
                .body(
                        "status", Matchers.is(expectedStatus.value()),
                        "type", Matchers.is("/errors/" + expectedType),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue(),
                        "$", Matchers.not(Matchers.hasKey("fields"))
                );
    }

    @Test
    void findCustomersContract(){
        final var params = new CustomerFilter(0, 5);
        final var output = CustomerSummaryOutputDataBuilder.builder()
                .buildCollection(2).stream().toList();
        when(queryService.filter(any(CustomerFilter.class))).thenReturn(new PageImpl<>(output.stream().toList()));
        RestAssuredMockMvc
            .given()
                .accept(APPLICATION_JSON_VALUE)
                .param("size", params.getSize())
                .param("page", params.getPage())
            .when()
                .get("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(APPLICATION_JSON_VALUE)
                .status(OK)
                .body(
                        "number", Matchers.equalTo(params.getPage()),
                        "size", Matchers.equalTo(output.size()),
                        "totalPages", Matchers.equalTo(1),
                        "totalElements", Matchers.equalTo(output.size()),

                        "content[0].id", Matchers.equalTo(output.getFirst().getId().toString()),
                        "content[0].firstName", Matchers.is(output.getFirst().getFirstName()),
                        "content[0].lastName", Matchers.is(output.getFirst().getLastName()),
                        "content[0].email", Matchers.is(output.getFirst().getEmail()),
                        "content[0].document", Matchers.is(output.getFirst().getDocument()),
                        "content[0].phone", Matchers.is(output.getFirst().getPhone()),
                        "content[0].birthDate", Matchers.is(output.getFirst().getBirthDate().toString()),
                        "content[0].loyaltyPoints", Matchers.is(output.getFirst().getLoyaltyPoints()),
                        "content[0].promotionNotificationsAllowed", Matchers.is(output.getFirst().isPromotionNotificationsAllowed()),
                        "content[0].archived", Matchers.is(output.getFirst().isArchived()),
                        "content[0].registeredAt", Matchers.is(formatter.format(output.getFirst().getRegisteredAt())),

                        "content[1].id", Matchers.equalTo(output.getLast().getId().toString()),
                        "content[1].firstName", Matchers.is(output.getLast().getFirstName()),
                        "content[1].lastName", Matchers.is(output.getLast().getLastName()),
                        "content[1].email", Matchers.is(output.getLast().getEmail()),
                        "content[1].document", Matchers.is(output.getLast().getDocument()),
                        "content[1].phone", Matchers.is(output.getLast().getPhone()),
                        "content[1].birthDate", Matchers.is(output.getLast().getBirthDate().toString()),
                        "content[1].loyaltyPoints", Matchers.is(output.getLast().getLoyaltyPoints()),
                        "content[1].promotionNotificationsAllowed", Matchers.is(output.getLast().isPromotionNotificationsAllowed()),
                        "content[1].archived", Matchers.is(output.getLast().isArchived()),
                        "content[1].registeredAt", Matchers.is(formatter.format(output.getLast().getRegisteredAt()))
                );

    }

    @Test
    void findByIdContract(){
        final var output = CustomerOutputDataBuilder.builder().build();
        final var outputAddress = output.getAddress();
        when(queryService.findById(any(UUID.class))).thenReturn(output);
        RestAssuredMockMvc
            .given()
                .accept(APPLICATION_JSON_VALUE)
            .when()
                .get("/api/v1/customers/{id}", output.getId())
            .then()
                .assertThat()
                .contentType(APPLICATION_JSON_VALUE)
                .status(OK)
                .body(
                        "id", Matchers.equalTo(output.getId().toString()),
                        "firstName", Matchers.equalTo(output.getFirstName()),
                        "lastName", Matchers.is(output.getLastName()),
                        "email", Matchers.is(output.getEmail()),
                        "document", Matchers.is(output.getDocument()),
                        "phone", Matchers.is(output.getPhone()),
                        "birthDate", Matchers.is(output.getBirthDate().toString()),
                        "loyaltyPoints", Matchers.is(output.getLoyaltyPoints()),
                        "promotionNotificationsAllowed", Matchers.is(output.isPromotionNotificationsAllowed()),
                        "archived", Matchers.is(output.getArchived()),
                        "registeredAt", Matchers.is(formatter.format(output.getRegisteredAt())),
                        "address.street", Matchers.is(outputAddress.getStreet()),
                        "address.number", Matchers.is(outputAddress.getNumber()),
                        "address.complement", Matchers.is(outputAddress.getComplement()),
                        "address.neighborhood", Matchers.is(outputAddress.getNeighborhood()),
                        "address.city", Matchers.is(outputAddress.getCity()),
                        "address.state", Matchers.is(outputAddress.getState()),
                        "address.zipCode", Matchers.is(outputAddress.getZipCode())
                );
    }

    @Test
    void findByIdErrorContract(){
        final var id = UUID.randomUUID();
        when(queryService.findById(id)).thenThrow(new CustomerNotFoundException());
        RestAssuredMockMvc
                .given()
                .accept(APPLICATION_JSON_VALUE)
                .when()
                .get("/api/v1/customers/{id}", id)
                .then()
                .assertThat()
                .contentType(APPLICATION_PROBLEM_JSON_VALUE)
                .status(NOT_FOUND)
                .body(
                        "status", Matchers.is(NOT_FOUND.value()),
                        "type", Matchers.is("/errors/not-found"),
                        "title", Matchers.notNullValue(),
                        "instance", Matchers.notNullValue(),
                        "$", Matchers.not(Matchers.hasKey("fields"))
                );
    }

}