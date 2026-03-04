package com.bracits.surveyengine.config;

import com.bracits.surveyengine.common.jackson.FlexibleInstantDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Instant;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .addModule(new SimpleModule()
                        .addDeserializer(Instant.class, new FlexibleInstantDeserializer()))
                .build();
    }
}
