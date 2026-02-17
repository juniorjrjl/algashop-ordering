package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderPersistenceEntityRepository persistenceEntityRepository;
    private final OrderPersistenceEntityDisassembler disassembler;

    @Override
    public OrderDetailOutput findById(final String id) {
        return persistenceEntityRepository.findById(new OrderId(id).value().toLong())
                .map(disassembler::toDetailOutput)
                .orElseThrow(OrderNotFoundException::new);
    }

}
