package com.algaworks.algashop.ordering.infrastructure.adapter.in.web.shppingcart;

import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartItemOutput;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ShoppingCartItemListModel {
    private List<ShoppingCartItemOutput> items = new ArrayList<>();
}