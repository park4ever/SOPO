package com.sopo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SopoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SopoApplication.class, args);
	}

}
