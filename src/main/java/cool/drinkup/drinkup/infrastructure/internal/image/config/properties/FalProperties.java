package cool.drinkup.drinkup.infrastructure.internal.image.config.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "image.fal")
public class FalProperties {
    private String apiKey = "cc3ee9ca-9709-4efb-891a-2ca9bb762b52:a5b61857ca45c3e96e747081d4463415";
    private String endpointId = "fal-ai/fast-sdxl";
    private Integer timeout = 30000;

    /**
     * 兜底出图模型：主模型（fast-sdxl + LoRA）失败时使用。不依赖 LoRA，靠风格词还原水彩插画风。
     * 可通过 image.fal.fallback-endpoint-id 切换更便宜的模型（如 fal-ai/flux/schnell）。
     */
    private String fallbackEndpointId = "fal-ai/flux-pro";

    /**
     * 兜底模型的预设风格词（拼在 AI 返回的 image prompt 前面），用于在没有 LoRA 时保持风格统一。
     */
    private String fallbackStylePrompt =
            "flat 2D watercolor illustration, soft pastel colors, hand-drawn style, clean solid pastel background, no people";

    private ImageGenerationRequest imageProperties = new ImageGenerationRequest();

    /**
     * 图片生成请求配置
     */
    @Data
    public static class ImageGenerationRequest {
        /**
         * 提示词
         */
        private String prompt;

        @JsonProperty("negative_prompt")
        private String negativePrompt = "bad, messy, ugly, people";

        @JsonProperty("image_size")
        private Object imageSize = "landscape_4_3";

        @JsonProperty("num_inference_steps")
        private Integer numInferenceSteps = 25;

        @JsonProperty("guidance_scale")
        private Double guidanceScale = 7.5;

        @JsonProperty("num_images")
        private Integer numImages = 1;

        @JsonProperty("loras")
        private List<LoraWeight> loras = Collections.singletonList(new LoraWeight());

        @JsonProperty("embeddings")
        private List<Embedding> embeddings = new ArrayList<>();

        @JsonProperty("enable_safety_checker")
        private Boolean enableSafetyChecker = true;

        @JsonProperty("safety_checker_version")
        private String safetyCheckerVersion = "v1";

        @JsonProperty("format")
        private String format = "jpeg";

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class LoraWeight {
            @Builder.Default
            private String path =
                    "https://huggingface.co/alvdansen/dimension-w/resolve/main/araminta_k_dimension_w_XL.safetensors";
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Embedding {
            private String path;
        }
    }
}
