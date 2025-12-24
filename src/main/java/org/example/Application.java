package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * This is the entry point for the Future You Simulator application.
 * 
 * Note: @SpringBootApplication already includes:
 * - @EnableAutoConfiguration (which enables JPA repositories)
 * - @ComponentScan (which scans for repositories in the same package and sub-packages)
 * - @Configuration
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

