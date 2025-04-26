package cool.drinkup.drinkup.external.image;

import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.annotation.NewSpan;

public interface ImageGenerator {
    @Observed(name = "image.generate")
    @Timed(value = "image.generate", longTask = true)
    @NewSpan
    public String generateImage(String prompt);
}
