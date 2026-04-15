package com.Universal_bot_jobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class UniversalBotJobsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniversalBotJobsApplication.class, args);
	}

}
