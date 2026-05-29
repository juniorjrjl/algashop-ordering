package com.algaworks.algashop.ordering.contract.base;

import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.presentation.shoppingcart.ShoppingCartController;
import com.algaworks.algashop.ordering.utility.databuilder.application.ShoppingCartOutputDataBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebMvcTest(controllers = ShoppingCartController.class)
public class ShoppingCartBase {

    @Autowired
    private WebApplicationContext context;
    @MockitoBean
    private ShoppingCartManagementApplicationService managementService;
    @MockitoBean
    private ShoppingCartQueryService queryService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context)
                        .defaultResponseCharacterEncoding(UTF_8)
                        .build()
        );

        final var validShoppingCartId = UUID.fromString("ad265aa3-c77d-46e9-9782-b70c487c1e17");

        final var notFoundShoppingCartId = UUID.fromString("e2103964-5353-4910-81ee-212a40a2ca70");

        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        when(queryService.findById(validShoppingCartId))
                .thenReturn(ShoppingCartOutputDataBuilder.builder().build());

        when(queryService.findById(notFoundShoppingCartId))
                .thenThrow(new ShoppingCartNotFoundException());

        when(managementService.createNew(any(UUID.class)))
                .thenReturn(validShoppingCartId);
    }

}
