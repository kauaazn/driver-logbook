package com.portfolio.driverlogbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DriverLogbookApplication {
    public static void main(String[] args) { SpringApplication.run(DriverLogbookApplication.class, args); }
    @Bean WebMvcConfigurer corsConfigurer(@Value("${app.cors.allowed-origins}") String[] allowedOrigins) { return new WebMvcConfigurer() {
        @Override public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**").allowedOrigins(allowedOrigins).allowedMethods("GET", "POST", "PATCH", "OPTIONS").allowedHeaders("*");
        }
    }; }
}
