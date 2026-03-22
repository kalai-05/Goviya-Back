package com.goviya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableScheduling
@EnableMongoAuditing
public class GoviyaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoviyaBackendApplication.class, args);
	}

}
