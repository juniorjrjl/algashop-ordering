package com.algaworks.algashop.ordering.infrastructure.shipping.client.rapidex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(
        basePackageClasses = RapiDexAPIClient.class,
        types = RapiDexAPIClient.class,
        group = "RapiDexApi")
public class RapiDexAPIClientConfig {

    @Bean
    RestClientCustomizer restClientCustomizer(@Value("${algashop.integrations.rapidex.url}")
                                              final String baseUrl) {
        return restClientBuilder -> restClientBuilder.baseUrl(baseUrl);
    }

}

