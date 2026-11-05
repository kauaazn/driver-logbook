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
    @Bean WebMvcConfigurer corsConfigurer(@Value("${CORS_ALLOWED_ORIGIN:http://localhost:3000}") String allowedOrigin) { return new WebMvcConfigurer() {
        @Override public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**").allowedOrigins(allowedOrigin).allowedMethods("GET", "POST", "PATCH", "OPTIONS").allowedHeaders("*");
        }
    }; }
}
