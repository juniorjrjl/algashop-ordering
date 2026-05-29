package com.algaworks.algashop.ordering.utility.extension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@ExtendWith(PostgreSQLTestContainerExtension.class)
//@ContextConfiguration(initializers = PostgreSQLTestContainerExtension.Initializer.class)
public @interface PostgreSQLExtensionWithContextConfig {
}
