package com.algaworks.algashop.ordering;

import com.algaworks.algashop.ordering.infrastructure.config.FreeShippingConfig;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.ZoneOffset;
import java.util.TimeZone;

import static lombok.AccessLevel.PRIVATE;

@SpringBootApplication
@NoArgsConstructor(access = PRIVATE)
@EnableConfigurationProperties(FreeShippingConfig.class)
public class OrderingApplication {

	static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
		SpringApplication.run(OrderingApplication.class, args);
	}

}
