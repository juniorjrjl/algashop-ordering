package com.algaworks.algashop.ordering.application.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort.Direction;

import static lombok.AccessLevel.PROTECTED;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class SortablePageFilter<T> extends PageFilter{

    @Nullable
    @Getter(PROTECTED)
    private T sortByProperty;
    @Getter(PROTECTED)
    @Nullable
    private Direction sortDirection;

    protected SortablePageFilter(int pageNumber, int pageSize) {
        super(pageSize, pageNumber);
    }

    public abstract T getSortByPropertOrDefault();

    public abstract Direction getSortDirectionOrDefault();

}
