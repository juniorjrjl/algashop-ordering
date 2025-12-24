package com.algaworks.algashop.ordering;

import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static lombok.AccessLevel.PRIVATE;

@SpringBootApplication
@NoArgsConstructor(access = PRIVATE)
public class OrderingApplication {

	static void main(String[] args) {
		SpringApplication.run(OrderingApplication.class, args);
	}

}
