package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final CustomerPersistenceEntityRepository persistenceEntityRepository;
    private final EntityManager entityManager;

    private static final String FIND_BY_ID_AS_OUTPUT_JPQL = """
            SELECT new com.algaworks.algashop.ordering.application.customer.query.CustomerOutput(
                c.id,
                c.firstName,
                c.lastName,
                c.email,
                c.phone,
                c.document,
                c.birthDate,
                c.promotionNotificationsAllowed,
                c.loyaltyPoints,
                c.registeredAt,
                c.archivedAt,
                c.archived,
                new com.algaworks.algashop.ordering.application.common.AddressData(
                    c.address.street,
                    c.address.number,
                    c.address.city,
                    c.address.state,
                    c.address.zipCode,
                    c.address.complement,
                    c.address.neighborhood
                )
            )
            FROM CustomerPersistenceEntity c
            WHERE c.id = :id""";

    @Override
    public CustomerOutput findById(final UUID customerId) {
        try{
            final var query = entityManager.createQuery(FIND_BY_ID_AS_OUTPUT_JPQL, CustomerOutput.class);
            query.setParameter("id", customerId);
            return query.getSingleResult();
        } catch (NoResultException _){
            throw new CustomerNotFoundException();
        }
    }

}
