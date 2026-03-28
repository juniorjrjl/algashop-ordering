package com.algaworks.algashop.ordering.contract.base;

import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.presentation.OrderController;
import com.algaworks.algashop.ordering.utility.databuilder.presentation.OrderDetailOutputDataBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.when;

@WebMvcTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
public class OrderBase {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockitoBean
    protected OrderQueryService queryService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(webApplicationContext).defaultResponseCharacterEncoding(UTF_8)
                        .build()
        );
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        when(queryService.findById("01226N0640J7Q")).thenReturn(OrderDetailOutputDataBuilder.builder()
                        .withId(() -> "01226N0640J7Q")
                .build());
        when(queryService.findById("01226N0693HDH")).thenThrow(new OrderNotFoundException());
    }

}
