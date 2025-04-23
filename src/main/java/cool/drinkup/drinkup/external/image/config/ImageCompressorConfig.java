package cool.drinkup.drinkup.external.image.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cool.drinkup.drinkup.external.image.ImageCompressor;
import cool.drinkup.drinkup.external.image.config.properties.ImgProxyProperties;
import cool.drinkup.drinkup.external.image.impl.ImgProxyImageCompressor;

@Configuration
public class ImageCompressorConfig {

    @Bean
    public ImageCompressor imgProxyImageCompressor(ImgProxyProperties properties) {
        return new ImgProxyImageCompressor(properties);
    }
}
