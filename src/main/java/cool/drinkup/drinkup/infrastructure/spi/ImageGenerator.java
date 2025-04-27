package cool.drinkup.drinkup.infrastructure.spi;

import io.micrometer.tracing.annotation.NewSpan;

public interface ImageGenerator {
    @NewSpan
    public String generateImage(String prompt);
}
