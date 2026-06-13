package com.algaworks.algashop.ordering.infrastructure.config.locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.Locale;

@Configuration
public class FixedLocaleConfig {

    @Bean
    LocaleContextResolver localeResolver() {
        final var resolver = new FixedLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }

}
