package com.algaworks.algashop.ordering.infrastructure.adapter.in.web.shppingcart;

import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ForManagingShoppingCart;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartOutput;
import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ForQueryingShoppingCart;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.infrastructure.adapter.in.web.exceptionhandler.UnprocessableEntityException;
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

	private final ForManagingShoppingCart forManaging;
	private final ForQueryingShoppingCart forQuerying;

	@PostMapping
	@ResponseStatus(CREATED)
	public ShoppingCartOutput create(@RequestBody @Valid final ShoppingCartInput input) {
		final UUID shoppingCartId;
		try {
			shoppingCartId = forManaging.createNew(input.getCustomerId());
		} catch (final CustomerNotFoundException e) {
			throw new UnprocessableEntityException(e.getMessage(), e);
		}
		return forQuerying.findById(shoppingCartId);
	}

	@GetMapping("/{id}")
	public ShoppingCartOutput getById(@PathVariable final UUID id) {
		return forQuerying.findById(id);
	}

	@GetMapping("/{id}/items")
	public ShoppingCartItemListModel getItems(@PathVariable final UUID id) {
		var items = forQuerying.findById(id).getItems();
		return new ShoppingCartItemListModel(items);
	}

	@DeleteMapping("/{shoppingCartId}")
	@ResponseStatus(NO_CONTENT)
	public void delete(@PathVariable UUID shoppingCartId) {
		forManaging.delete(shoppingCartId);
	}

	@DeleteMapping("/{shoppingCartId}/items")
	@ResponseStatus(NO_CONTENT)
	public void empty(@PathVariable UUID shoppingCartId) {
		forManaging.empty(shoppingCartId);
	}

	@PostMapping("/{id}/items")
	@ResponseStatus(NO_CONTENT)
	public void addItem(@PathVariable UUID id,
		   			    @RequestBody @Valid final ShoppingCartItemInput input) {
		input.setShoppingCartId(id);
		try{
			forManaging.addItem(input);
		}catch (final ProductNotFoundException e) {
			throw new UnprocessableEntityException(e.getMessage(), e);
		}
	}

	@DeleteMapping("/{id}/items/{itemId}")
	@ResponseStatus(NO_CONTENT)
	public void removeItem(@PathVariable final UUID id,
						   @PathVariable final UUID itemId) {
		forManaging.removeItem(id, itemId);
	}
}