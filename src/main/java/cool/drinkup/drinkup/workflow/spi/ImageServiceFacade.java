package cool.drinkup.drinkup.workflow.spi;

import org.springframework.modulith.NamedInterface;

@NamedInterface("spi")
public interface ImageServiceFacade {
    String getImageUrl(String imageId);
}
