package cool.drinkup.drinkup.workflow.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "drinkup.bartender")
public class BartenderProperties {
    private String model = "deepseek/deepseek-chat-v3-0324";
    private Double temperature = 0.8;
}