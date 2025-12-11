package com.reliaquest.api.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Value("${mock.service.url}")
    private String url;

    @Bean
    public WebClient getClient(){
        return WebClient.builder().baseUrl(url).build();
    }
}
