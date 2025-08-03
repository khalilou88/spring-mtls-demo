package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Configuration
@Component
public class ClientRunner implements CommandLineRunner {

    private final RestTemplate restTemplate;

    public ClientRunner(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Attempting to call mTLS server...");
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("https://localhost:8443/hello", String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Received response: " + response.getBody());
            } else {
                System.err.println("Request failed with status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Failed to connect to mTLS server. Cause: " + e.getMessage());
        }
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, SslBundles sslBundles) {
        return restTemplateBuilder
                .setSslBundle(sslBundles.getBundle("client-bundle")) // Explicitly use the client's SSL bundle
                .build();
    }
}