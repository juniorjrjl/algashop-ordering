package com.algaworks.algashop.ordering.infrastructure.beans;

import com.algaworks.algashop.ordering.domain.model.order.CustomerHaveFreeShippingSpecification;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.infrastructure.config.FreeShippingConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpecificationBeanConfig {

    @Bean
    CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification(final Orders orders,
                                                                                final FreeShippingConfig freeShippingConfig) {
        return new CustomerHaveFreeShippingSpecification(
                orders,
                freeShippingConfig
        );
    }

}
