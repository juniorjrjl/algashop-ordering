package com.algaworks.algashop.ordering.infrastructure.product.client.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ProductCatalogAPIConfig {

    @Bean
    ProductCatalogAPIClient productCatalogAPIClient(final RestClient.Builder builder,
                                                    @Value("${algashop.integrations.product-catalog.url}")
                                                    final String url) {
        final var restClient = builder.baseUrl(url).build();
        final var adapter = RestClientAdapter.create(restClient);
        final var proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return proxyFactory.createClient(ProductCatalogAPIClient.class);
    }

}
