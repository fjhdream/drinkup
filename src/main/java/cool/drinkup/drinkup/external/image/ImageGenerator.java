package cool.drinkup.drinkup.external.image;

import io.micrometer.tracing.annotation.NewSpan;

public interface ImageGenerator {
    @NewSpan
    public String generateImage(String prompt);
}
