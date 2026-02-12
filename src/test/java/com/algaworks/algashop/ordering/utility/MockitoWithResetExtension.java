package com.algaworks.algashop.ordering.utility;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Objects.nonNull;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.reset;

public class MockitoWithResetExtension extends MockitoExtension {


    @Override
    public void afterEach(final ExtensionContext context) {
        resetMocks(context.getRequiredTestInstance());
        super.afterEach(context);
    }

    private static void resetMocks(final Object testInstance) {
        var clazz = testInstance.getClass();

        while (clazz != Object.class) {
            for (var field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                try {
                    final var value = field.get(testInstance);
                    if (nonNull(value) && mockingDetails(value).isMock()) {
                        reset(value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }


}
