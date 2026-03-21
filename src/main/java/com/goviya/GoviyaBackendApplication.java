package com.goviya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GoviyaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoviyaBackendApplication.class, args);
	}

}
