package com.BYD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.BYD.model")
@EnableJpaRepositories(basePackages = "com.BYD.repository")
public class JwtAuthenticationApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtAuthenticationApplication.class, args);
    }
}