package com.algaworks.algashop.ordering.infrastructure.beans;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(final ApiVersionConfigurer configurer) {
        configurer
                .usePathSegment(1)
                .addSupportedVersions("1")
                .setDefaultVersion("1");
    }
}
