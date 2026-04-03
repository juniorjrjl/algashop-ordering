package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/{version}/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {

	private final ShoppingCartManagementApplicationService managementService;
	private final ShoppingCartQueryService queryService;

	@PostMapping
	@ResponseStatus(CREATED)
	public ShoppingCartOutput create(@RequestBody @Valid final ShoppingCartInput input) {
		final var shoppingCartId = managementService.createNew(input.getCustomerId());
		return queryService.findById(shoppingCartId);
	}

	@GetMapping("/{id}")
	public ShoppingCartOutput getById(@PathVariable final UUID id) {
		return queryService.findById(id);
	}

	@GetMapping("/{id}/items")
	public ShoppingCartItemListModel getItems(@PathVariable final UUID id) {
		var items = queryService.findById(id).getItems();
		return new ShoppingCartItemListModel(items);
	}

	@DeleteMapping("/{shoppingCartId}")
	@ResponseStatus(NO_CONTENT)
	public void delete(@PathVariable UUID shoppingCartId) {
		managementService.delete(shoppingCartId);
	}

	@DeleteMapping("/{shoppingCartId}/items")
	@ResponseStatus(NO_CONTENT)
	public void empty(@PathVariable UUID shoppingCartId) {
		managementService.empty(shoppingCartId);
	}

	@PostMapping("/{id}/items")
	@ResponseStatus(NO_CONTENT)
	public void addItem(@PathVariable UUID id,
		   			    @RequestBody @Valid final ShoppingCartItemInput input) {
		input.setShoppingCartId(id);
		managementService.addItem(input);
	}

	@DeleteMapping("/{id}/items/{itemId}")
	@ResponseStatus(NO_CONTENT)
	public void removeItem(@PathVariable final UUID id,
						   @PathVariable final UUID itemId) {
		managementService.removeItem(id, itemId);
	}
}