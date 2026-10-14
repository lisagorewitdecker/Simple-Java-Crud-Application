package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Demo Spring Boot application.
 */
@SpringBootApplication
public class DemoApplication {

	public DemoApplication() {
		super();
	}

	/**
	 * Launches the Spring Boot application context.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}