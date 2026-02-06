package com.algaworks.algashop.ordering.utility;

import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import net.datafaker.providers.base.AbstractProvider;

public class ProductProvider extends AbstractProvider<CustomFaker> {

    protected ProductProvider(final CustomFaker faker) {
        super(faker);
    }

    public ProductName productName() {
        return new ProductName(faker.book().title());
    }

}
