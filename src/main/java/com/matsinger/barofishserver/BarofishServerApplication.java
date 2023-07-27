package com.matsinger.barofishserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BarofishServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarofishServerApplication.class, args);
	}

}
