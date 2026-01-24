package com.algaworks.algashop.ordering.infrastructure.beans;

import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.springframework.context.annotation.FilterType.ANNOTATION;

@Configuration
@ComponentScan(
        basePackages = "com.algaworks.algashop.ordering.domain.model",
        includeFilters = @ComponentScan.Filter(type = ANNOTATION, classes = DomainService.class)
)
public class DomainServiceScanConfig {

}
