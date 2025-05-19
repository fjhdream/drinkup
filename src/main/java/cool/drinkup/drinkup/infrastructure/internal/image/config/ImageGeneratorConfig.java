package cool.drinkup.drinkup.infrastructure.internal.image.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import cool.drinkup.drinkup.infrastructure.spi.ImageGenerator;
import cool.drinkup.drinkup.infrastructure.internal.image.config.properties.GlifProperties;
import cool.drinkup.drinkup.infrastructure.internal.image.impl.GlifImageGenerator;

@Configuration
public class ImageGeneratorConfig {

    @Bean
    public ImageGenerator glifImageGenerator(GlifProperties properties, ObjectMapper objectMapper) {
        return new GlifImageGenerator(properties, objectMapper);
    }
} 