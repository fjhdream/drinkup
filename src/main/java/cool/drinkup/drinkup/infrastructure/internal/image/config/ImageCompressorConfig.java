package cool.drinkup.drinkup.infrastructure.internal.image.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cool.drinkup.drinkup.infrastructure.spi.ImageCompressor;
import cool.drinkup.drinkup.infrastructure.internal.image.config.properties.ImgProxyProperties;
import cool.drinkup.drinkup.infrastructure.internal.image.impl.ImgProxyImageCompressor;

@Configuration
public class ImageCompressorConfig {

    @Bean
    public ImageCompressor imgProxyImageCompressor(ImgProxyProperties properties) {
        return new ImgProxyImageCompressor(properties);
    }
}
