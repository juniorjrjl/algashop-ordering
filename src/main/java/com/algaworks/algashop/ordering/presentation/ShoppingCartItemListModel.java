package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ShoppingCartItemListModel {
    private List<ShoppingCartItemOutput> items = new ArrayList<>();
}