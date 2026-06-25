package cool.drinkup.drinkup.infrastructure.internal.image.config;

import cool.drinkup.drinkup.infrastructure.internal.image.config.properties.FalProperties;
import cool.drinkup.drinkup.infrastructure.internal.image.impl.fal.FalImageGeneratorFactory;
import cool.drinkup.drinkup.infrastructure.spi.image.ImageGenerator;
import cool.drinkup.drinkup.infrastructure.spi.image.config.FalConfig;
import java.util.Collections;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ImageGeneratorConfig {

    @Resource
    private FalImageGeneratorFactory falImageGeneratorFactory;

    /**
     * 兜底出图生成器：主模型（fast-sdxl + LoRA）失败时使用。
     * 用 flux-pro 等通用模型 + 预设水彩风格词，不依赖 LoRA，绕开 LoRA 加载失败导致的 422。
     */
    @Bean
    @Primary
    public ImageGenerator falFallbackImageGenerator(FalProperties falProperties) {
        FalProperties.ImageGenerationRequest props = new FalProperties.ImageGenerationRequest();
        // 兜底模型不挂 LoRA / embedding（flux 不支持 SDXL 的 LoRA，也避开 LoRA 加载失败）
        props.setLoras(Collections.emptyList());
        props.setEmbeddings(Collections.emptyList());

        FalConfig config = FalConfig.builder()
                .apiKey(falProperties.getApiKey())
                .endpointId(falProperties.getFallbackEndpointId())
                .timeout(falProperties.getTimeout())
                .triggerWord(falProperties.getFallbackStylePrompt())
                .imageProperties(props)
                .build();
        return falImageGeneratorFactory.create(config);
    }
}
