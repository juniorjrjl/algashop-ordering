package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {

    private final ShoppingCartPersistenceEntityRepository persistenceEntityRepository;
    private final ShoppingCartPersistenceEntityDisassembler disassembler;

    @Override
    public ShoppingCartOutput findById(final UUID id) {
        return persistenceEntityRepository.findById(id)
                .map(disassembler::toOutput)
                .orElseThrow(ShoppingCartNotFound::new);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(final UUID customerId) {
        return persistenceEntityRepository.findByCustomerId(customerId)
                .map(disassembler::toOutput)
                .orElseThrow(ShoppingCartNotFound::new);
    }

}
