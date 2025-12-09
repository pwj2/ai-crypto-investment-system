package com.digitalcoin.holdings_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.digitalcoin.holdings_service.entity")
@EnableJpaRepositories("com.digitalcoin.holdings_service.repository")
@EnableScheduling
public class
HoldingsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoldingsServiceApplication.class, args);
	}

}
