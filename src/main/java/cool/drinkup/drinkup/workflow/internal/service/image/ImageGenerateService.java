package cool.drinkup.drinkup.workflow.internal.service.image;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import cool.drinkup.drinkup.infrastructure.spi.ImageGenerator;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.RetryableException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerateService {

    @Qualifier("glifImageGenerator")
    private final ImageGenerator glifImageGenerator;

    @Qualifier("falImageGenerator")
    private final ImageGenerator falImageGenerator;

    @Retryable(
        value = {RetryableException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    @Observed(name = "image.generate",
                contextualName = "生成图片",
            lowCardinalityKeyValues = {
                "Tag", "image",
                "Server", "glif"
            })
    public String generateImage(String prompt) {
        return glifImageGenerator.generateImage(prompt);
    }

    @Observed(name = "image.generate",
                contextualName = "生成图片",
            lowCardinalityKeyValues = {
                "Tag", "image",
                "Server", "fal"
            })
    @Recover
    public String recover(RetryableException e, String prompt) {
        log.warn("glif generate image failed, use fal to generate image");
        return falImageGenerator.generateImage(prompt);
    }


}
