package com.algaworks.algashop.ordering.infrastructure.adapter.out.persistence.shoppingcart;

import com.algaworks.algashop.ordering.core.port.in.shoppingcart.ShoppingCartOutput;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.core.port.out.shoppingcart.ForObtainingShoppingCart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ForObtainingShoppingCartJPARepositoryImpl implements ForObtainingShoppingCart {

    private final ShoppingCartPersistenceEntityRepository persistenceEntityRepository;
    private final ShoppingCartPersistenceEntityDisassembler disassembler;

    @Override
    public ShoppingCartOutput findById(final UUID id) {
        return persistenceEntityRepository.findById(id)
                .map(disassembler::toOutput)
                .orElseThrow(ShoppingCartNotFoundException::new);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(final UUID customerId) {
        return persistenceEntityRepository.findByCustomerId(customerId)
                .map(disassembler::toOutput)
                .orElseThrow(ShoppingCartNotFoundException::new);
    }

}
