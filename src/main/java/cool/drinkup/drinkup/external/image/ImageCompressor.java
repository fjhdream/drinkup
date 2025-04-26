package cool.drinkup.drinkup.external.image;

import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.annotation.NewSpan;

public interface ImageCompressor {
    @Observed(name = "image.compress")
    @Timed(value = "image.compress", longTask = true)
    @NewSpan
    public String compress(String imageUrl);
}