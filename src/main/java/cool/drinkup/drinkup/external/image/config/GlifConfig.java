package cool.drinkup.drinkup.external.image.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import cool.drinkup.drinkup.external.image.ImageGenerator;
import cool.drinkup.drinkup.external.image.impl.GlifImageGenerator;

@Configuration
public class GlifConfig {

    @Bean
    public ImageGenerator glifImageGenerator(GlifProperties properties, ObjectMapper objectMapper) {
        return new GlifImageGenerator(properties, objectMapper);
    }
} 