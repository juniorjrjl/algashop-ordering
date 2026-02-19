package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

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

    @Override
    public Page<CustomerSummaryOutput> filter(final CustomerFilter filter) {
        final var totalQueryResults = countTotalQueryResults(filter);
        if (totalQueryResults == 0){
            final var pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }
        return filterQuery(filter, totalQueryResults);
    }

    private Page<CustomerSummaryOutput> filterQuery(final CustomerFilter filter, final long totalQueryResults) {
        final var builder = entityManager.getCriteriaBuilder();
        final var criteriaQuery = builder.createQuery(CustomerSummaryOutput.class);
        final var root = criteriaQuery.from(CustomerPersistenceEntity.class);

        criteriaQuery.select(
                builder.construct(CustomerSummaryOutput.class,
                        root.get(CustomerPersistenceEntity_.id),
                        root.get(CustomerPersistenceEntity_.firstName),
                        root.get(CustomerPersistenceEntity_.lastName),
                        root.get(CustomerPersistenceEntity_.email),
                        root.get(CustomerPersistenceEntity_.document),
                        root.get(CustomerPersistenceEntity_.phone),
                        root.get(CustomerPersistenceEntity_.birthDate),
                        root.get(CustomerPersistenceEntity_.loyaltyPoints),
                        root.get(CustomerPersistenceEntity_.registeredAt),
                        root.get(CustomerPersistenceEntity_.archivedAt),
                        root.get(CustomerPersistenceEntity_.promotionNotificationsAllowed),
                        root.get(CustomerPersistenceEntity_.archived)
                )
        );

        final var predicates = toPredicates(builder, root, filter);
        final var sortOrder = toSortOrder(builder, root, filter);
        criteriaQuery.where(predicates);
        criteriaQuery.orderBy(sortOrder);
        final var typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(filter.getPage() * filter.getSize());
        typedQuery.setMaxResults(filter.getSize());
        final var pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        return new PageImpl<>(typedQuery.getResultList(), pageRequest, totalQueryResults);
    }

    private Order toSortOrder(final CriteriaBuilder builder,
                                 final Root<CustomerPersistenceEntity> root,
                                 final CustomerFilter filter) {
        return filter.getSortDirectionOrDefault() == Sort.Direction.ASC ?
                builder.asc(root.get(toSortField(filter.getSortByPropertOrDefault()))) :
                builder.desc(root.get(toSortField(filter.getSortByPropertOrDefault())));
    }

    private SingularAttribute<CustomerPersistenceEntity, ?> toSortField(final CustomerFilter.SortType sortType) {
        return switch (sortType){
            case FIRST_NAME -> CustomerPersistenceEntity_.firstName;
            case REGISTERED_AT -> CustomerPersistenceEntity_.registeredAt;
        };
    }

    private long countTotalQueryResults(final CustomerFilter filter) {
        final var builder = entityManager.getCriteriaBuilder();
        final var criteriaQuery = builder.createQuery(Long.class);
        final var root = criteriaQuery.from(CustomerPersistenceEntity.class);

        final var count = builder.count(root);
        final var predicates = toPredicates(builder, root, filter);
        criteriaQuery.where(predicates);
        criteriaQuery.select(count);

        final var query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate[] toPredicates(final CriteriaBuilder builder,
                                     final Root<CustomerPersistenceEntity> root,
                                     final CustomerFilter filter) {
        final List<Predicate> predicates = new ArrayList<>();
        if (nonNull(filter.getFirstName())) {
            final var predicate = builder.like(
                    builder.lower(root.get(CustomerPersistenceEntity_.firstName)),
                    filter.getFirstName().toLowerCase() + "%"
            );
            predicates.add(predicate);
        }
        if (nonNull(filter.getEmail())) {
            final var predicate = builder.like(
                    root.get(CustomerPersistenceEntity_.email),
                    "%" + filter.getEmail() + "%"
            );
            predicates.add(predicate);
        }
        return predicates.toArray(new Predicate[0]);
    }

}
