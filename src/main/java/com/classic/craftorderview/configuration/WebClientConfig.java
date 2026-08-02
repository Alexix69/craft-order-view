package com.classic.craftorderview.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

import java.util.TimeZone;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient webClient() {
        JsonMapper jsonMapper = JsonMapper.builder()
                .defaultTimeZone(TimeZone.getTimeZone("America/Guayaquil"))
                .build();
        return WebClient.builder()
                .baseUrl("http://localhost:8080/api")
                .codecs(configurer -> {
                    configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                    configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                })
                .build();
    }
}
