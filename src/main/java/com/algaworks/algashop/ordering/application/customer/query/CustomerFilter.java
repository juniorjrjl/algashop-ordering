package com.algaworks.algashop.ordering.application.customer.query;

import com.algaworks.algashop.ordering.application.common.SortablePageFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilter extends SortablePageFilter<CustomerFilter.SortType> {

    @Nullable
    private String firstName;
    @Nullable
    private String email;

    public CustomerFilter(final int pageNumber, final int pageSize) {
        super(pageNumber, pageSize);
    }

    @Override
    public SortType getSortByPropertOrDefault() {
        return Optional.ofNullable(getSortByProperty()).orElse(SortType.REGISTERED_AT);
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return Optional.ofNullable(getSortDirection()).orElse(ASC);
    }

    public enum SortType {
        REGISTERED_AT,
        FIRST_NAME,
    }
}
