package com.creavispace.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class CreavispaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreavispaceApplication.class, args);
	}

}
