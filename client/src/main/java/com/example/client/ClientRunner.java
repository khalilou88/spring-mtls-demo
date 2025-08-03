package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * A component that runs on startup to call the mTLS-secured server.
 * It uses a RestTemplate that has been pre-configured with the necessary
 * SSL settings.
 */
@Component
public class ClientRunner implements CommandLineRunner {

    private final RestTemplate restTemplate;

    /**
     * The RestTemplate bean is automatically injected by Spring.
     * @param restTemplate The SSL-configured RestTemplate instance.
     */
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
}
