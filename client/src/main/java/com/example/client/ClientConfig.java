package com.example.client;

import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * A dedicated configuration class to define the RestTemplate bean.
 * This separation prevents a circular dependency with the ClientRunner.
 */
@Configuration
public class ClientConfig {

    /**
     * Creates and configures a RestTemplate bean with the specified SSL bundle.
     *
     * @param restTemplateBuilder The auto-configured builder for RestTemplate.
     * @param sslBundles The registry of SSL bundles from application properties.
     * @return A configured RestTemplate instance for making mTLS calls.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, SslBundles sslBundles) {
        // We explicitly get the "client-bundle" by name from the registry
        // and apply it to the RestTemplate.
        return restTemplateBuilder
                .setSslBundle(sslBundles.getBundle("client-bundle"))
                .build();
    }
}
