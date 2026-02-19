package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.order.query.CustomerMinimalOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderFilter;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity_;
import jakarta.persistence.EntityManager;
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

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final EntityManager entityManager;
    private final OrderPersistenceEntityRepository persistenceEntityRepository;
    private final OrderPersistenceEntityDisassembler disassembler;

    @Override
    public OrderDetailOutput findById(final String id) {
        return persistenceEntityRepository.findById(new OrderId(id).value().toLong())
                .map(disassembler::toDetailOutput)
                .orElseThrow(OrderNotFoundException::new);
    }

    @Override
    public Page<OrderSummaryOutput> filter(final OrderFilter filter) {
        final var totalQueryResults = countTotalQueryResults(filter);
        if (totalQueryResults == 0){
            final var pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }
        return filterQuery(filter, totalQueryResults);
    }

    private Page<OrderSummaryOutput> filterQuery(final OrderFilter filter,
                                                 final long totalQueryResults) {
        final var builder = entityManager.getCriteriaBuilder();
        final var criteriaQuery = builder.createQuery(OrderSummaryOutput.class);
        final var root = criteriaQuery.from(OrderPersistenceEntity.class);

        final var customer = root.get(OrderPersistenceEntity_.customer);
        criteriaQuery.select(
                builder.construct(OrderSummaryOutput.class,
                        root.get(OrderPersistenceEntity_.id),
                        root.get(OrderPersistenceEntity_.totalItems),
                        root.get(OrderPersistenceEntity_.totalAmount),
                        root.get(OrderPersistenceEntity_.placedAt),
                        root.get(OrderPersistenceEntity_.paidAt),
                        root.get(OrderPersistenceEntity_.canceledAt),
                        root.get(OrderPersistenceEntity_.readyAt),
                        root.get(OrderPersistenceEntity_.orderStatus),
                        root.get(OrderPersistenceEntity_.paymentMethod),
                        builder.construct(CustomerMinimalOutput.class,
                                customer.get(CustomerPersistenceEntity_.id),
                                customer.get(CustomerPersistenceEntity_.firstName),
                                customer.get(CustomerPersistenceEntity_.lastName),
                                customer.get(CustomerPersistenceEntity_.email),
                                customer.get(CustomerPersistenceEntity_.document),
                                customer.get(CustomerPersistenceEntity_.phone)
                        )
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
                              final Root<OrderPersistenceEntity> root,
                              final OrderFilter filter) {
        return filter.getSortDirectionOrDefault() == Sort.Direction.ASC ?
                builder.asc(root.get(toSortField(filter.getSortByPropertOrDefault()))) :
                builder.desc(root.get(toSortField(filter.getSortByPropertOrDefault())));
    }

    private SingularAttribute<OrderPersistenceEntity, ?> toSortField(final OrderFilter.SortType sortType) {
        return switch (sortType){
            case PAID_AT -> OrderPersistenceEntity_.paidAt;
            case READY_AT -> OrderPersistenceEntity_.readyAt;
            case PLACED_AT -> OrderPersistenceEntity_.placedAt;
            case CANCELED_AT -> OrderPersistenceEntity_.canceledAt;
            case ORDER_STATUS -> OrderPersistenceEntity_.orderStatus;
        };
    }

    private long countTotalQueryResults(final OrderFilter filter) {
        final var builder = entityManager.getCriteriaBuilder();
        final var criteriaQuery = builder.createQuery(Long.class);
        final var root = criteriaQuery.from(OrderPersistenceEntity.class);

        final var count = builder.count(root);
        final var predicates = toPredicates(builder, root, filter);
        criteriaQuery.where(predicates);
        criteriaQuery.select(count);

        final var query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate[] toPredicates(final CriteriaBuilder builder,
                                     final Root<OrderPersistenceEntity> root,
                                     final OrderFilter filter) {
        final List<Predicate> predicates = new ArrayList<>();
        if (nonNull(filter.getCustomerId())){
            final var predicate = builder.equal(
                    root.get(OrderPersistenceEntity_.customer).get(CustomerPersistenceEntity_.id),
                    filter.getCustomerId()
            );
            predicates.add(predicate);
        }
        if (nonNull(filter.getOrderStatus()) && !filter.getOrderStatus().isBlank()){
            final var predicate = builder.equal(
                    root.get(OrderPersistenceEntity_.orderStatus),
                    filter.getOrderStatus()
            );
            predicates.add(predicate);
        }
        if (nonNull(filter.getOrderId())){
            final var predicate = builder.equal(
                    root.get(OrderPersistenceEntity_.id),
                    filter.getOrderId()
            );
            predicates.add(predicate);
        }
        if (nonNull(filter.getPlacedAtFrom())){
            final var predicate = builder.greaterThanOrEqualTo(
                    root.get(OrderPersistenceEntity_.placedAt),
                    filter.getPlacedAtFrom()
            );
            predicates.add(predicate);
        }
        if (nonNull(filter.getPlacedAtTo())){
            final var predicate = builder.lessThanOrEqualTo(
                    root.get(OrderPersistenceEntity_.placedAt),
                    filter.getPlacedAtTo()
            );
            predicates.add(predicate);
        }
        if (nonNull(filter.getTotalAmountFrom())){
            final var predicate = builder.greaterThanOrEqualTo(
                    root.get(OrderPersistenceEntity_.totalAmount),
                    filter.getTotalAmountFrom()
            );
            predicates.add(predicate);
        }
        if (nonNull(filter.getTotalAmountTo())){
            final var predicate = builder.lessThanOrEqualTo(
                    root.get(OrderPersistenceEntity_.totalAmount),
                    filter.getTotalAmountTo()
            );
            predicates.add(predicate);
        }
        return predicates.toArray(new Predicate[0]);

    }

}
