package com.izayoi.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

	@Bean
	ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		return builder.serializationInclusion(JsonInclude.Include.NON_NULL).build();
	}
}
