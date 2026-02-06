package com.algaworks.algashop.ordering.infrastructure.persistence;

import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {

    @Bean
    ImplicitNamingStrategy implicit(){
        return new ImplicitNamingStrategyComponentPathImpl();
    }

}
