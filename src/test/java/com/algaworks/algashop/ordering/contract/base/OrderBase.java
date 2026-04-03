package com.algaworks.algashop.ordering.contract.base;

import com.algaworks.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.algaworks.algashop.ordering.application.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.algaworks.algashop.ordering.application.checkout.CheckoutInput;
import com.algaworks.algashop.ordering.application.order.query.OrderFilter;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.presentation.OrderController;
import com.algaworks.algashop.ordering.utility.databuilder.application.OrderSummaryOutputDataBuilder;
import com.algaworks.algashop.ordering.utility.databuilder.presentation.OrderDetailOutputDataBuilder;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
public class OrderBase {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockitoBean
    protected OrderQueryService queryService;

    @MockitoBean
    private BuyNowApplicationService buyNowApplicationService;

    @MockitoBean
    private CheckoutApplicationService checkoutApplicationService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(webApplicationContext).defaultResponseCharacterEncoding(UTF_8)
                        .build()
        );
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        findByIdMock();
        createOrder();
    }

    private void createOrder() {
        final var validOrderId = "01226N0640J7Q";
        when(buyNowApplicationService.buyNow(any(BuyNowInput.class)))
                .thenReturn(validOrderId);

        when(checkoutApplicationService.checkout(any(CheckoutInput.class)))
                .thenReturn(validOrderId);

        when(queryService.findById(validOrderId))
                .thenReturn(OrderDetailOutputDataBuilder.builder().build());
    }

    private void findByIdMock() {
        when(queryService.findById("01226N0640J7Q")).thenReturn(OrderDetailOutputDataBuilder.builder()
                .withId(() -> "01226N0640J7Q")
                .build());
        when(queryService.findById("01226N0693HDH")).thenThrow(new OrderNotFoundException());
    }

    private void filter(){
        when(queryService.filter(any(OrderFilter.class)))
                .thenReturn(new PageImpl<>(
                        List.of(OrderSummaryOutputDataBuilder.builder().build())
                ));
    }

}
