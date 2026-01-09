package com.algaworks.algashop.ordering.infrastructure.persistence.utility;

import com.algaworks.algashop.ordering.domain.model.entity.AggregateRoot;
import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PersistenceUtil {

    @SneakyThrows
    public static void updateVersion(final AggregateRoot<?> aggregateRoot, final Long currentVersion) {
        final var version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);
        ReflectionUtils.setField(version, aggregateRoot, currentVersion);
        version.setAccessible(false);
    }

}
