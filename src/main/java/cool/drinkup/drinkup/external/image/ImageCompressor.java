package cool.drinkup.drinkup.external.image;

import io.micrometer.tracing.annotation.NewSpan;

public interface ImageCompressor {

    @NewSpan
    public String compress(String imageUrl);
}