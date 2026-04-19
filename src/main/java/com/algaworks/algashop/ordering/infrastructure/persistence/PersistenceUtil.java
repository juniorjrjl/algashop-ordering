package com.algaworks.algashop.ordering.infrastructure.persistence;

import com.algaworks.algashop.ordering.domain.model.AggregateRoot;
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

    @SneakyThrows
    public static void updateValueObjectVersion(final AggregateRoot<?> aggregateRoot,
                                                final Object valueObjectId,
                                                final String methodName,
                                                final Long version) {
        final var changeVersionMethod = aggregateRoot.getClass().getDeclaredMethod(
                methodName,
                valueObjectId.getClass(),
                Long.class
        );
        changeVersionMethod.setAccessible(true);
        changeVersionMethod.invoke(aggregateRoot, valueObjectId, version);
        changeVersionMethod.setAccessible(false);

    }

}
