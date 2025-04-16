package cool.drinkup.drinkup.external.image.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "image.glif")
public class GlifProperties {
    private String apiUrl = "https://simple-api.glif.app";
    private String bearerToken;
    private String glifId;
    private String weights = "https://huggingface.co/alvdansen/softpasty-flux-dev/resolve/main/araminta_k_softpasty_diffusion_flux.safetensors";
} 