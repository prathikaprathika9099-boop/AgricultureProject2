package com.example.AgriProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AgriProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgriProjectApplication.class, args);
	}

}
