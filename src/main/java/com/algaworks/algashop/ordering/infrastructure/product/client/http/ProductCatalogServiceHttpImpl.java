package com.algaworks.algashop.ordering.infrastructure.product.client.http;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.presentation.BadGatewayException;
import com.algaworks.algashop.ordering.presentation.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogServiceHttpImpl implements ProductCatalogService {

    private final ProductCatalogAPIClient apiClient;

    @Override
    public Optional<Product> ofId(final ProductId productId) {
        final ProductResponse response;
        try {
            response = apiClient.getById(productId.value());
        } catch (final ResourceAccessException e){
            throw new GatewayTimeoutException(e.getMessage(), e);
        }catch (final HttpClientErrorException.NotFound _) {
            return Optional.empty();
        }catch (final HttpClientErrorException e) {
            throw new BadGatewayException(e.getMessage(), e);
        }
        return Optional.of(response).map(r -> Product.builder()
                .id(new ProductId(r.getId()))
                .name(new ProductName(r.getName()))
                .price(new Money(r.getSalePrice()))
                .inStock(r.getInStock())
                .build());
    }
}
