package cool.drinkup.drinkup.infrastructure.internal.image.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "image.fal")
public class FalProperties {
    private String apiKey;
    private String endpointId;
    private Integer timeout = 30000;
}
