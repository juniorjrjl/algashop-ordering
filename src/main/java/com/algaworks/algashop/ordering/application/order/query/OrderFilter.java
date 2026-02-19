package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.application.common.SortablePageFilter;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.algaworks.algashop.ordering.application.order.query.OrderFilter.SortType.PLACED_AT;
import static java.util.Objects.isNull;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderFilter extends SortablePageFilter<OrderFilter.SortType> {

    @Nullable
    private String orderStatus;
    @Nullable
    private String orderId;
    @Nullable
    private UUID customerId;
    @Nullable
    private OffsetDateTime placedAtFrom;
    @Nullable
    private OffsetDateTime placedAtTo;
    @Nullable
    private BigDecimal totalAmountFrom;
    @Nullable
    private BigDecimal totalAmountTo;

    public OrderFilter(int pageNumber, int pageSize) {
        super(pageNumber, pageSize);
    }

    @Nullable
    public Long getOrderId() {
        if (isNull(this.orderId)){
            return null;
        }
        return new OrderId(this.orderId).value().toLong();
    }

    @Override
    public OrderFilter.SortType getSortByPropertOrDefault() {
        return Optional.ofNullable(getSortByProperty()).orElse(PLACED_AT);
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return Optional.ofNullable(getSortDirection()).orElse(ASC);
    }

    public enum SortType {
        PLACED_AT,
        ORDER_STATUS,
        PAID_AT,
        READY_AT,
        CANCELED_AT;
    }
}
