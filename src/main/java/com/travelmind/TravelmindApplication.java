package com.travelmind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. @SpringBootApplication turns this class into a Spring Boot app:
 * it starts an embedded Tomcat server and auto-configures everything on the
 * classpath -- including the Spring AI Bedrock client.
 */
@SpringBootApplication
public class TravelmindApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelmindApplication.class, args);
    }
}
