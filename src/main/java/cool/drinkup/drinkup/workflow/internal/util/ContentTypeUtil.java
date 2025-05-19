package cool.drinkup.drinkup.workflow.internal.util;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContentTypeUtil {
    
    private final ServletContext servletContext;
    
    public String detectMimeType(Resource resource) throws IOException {
        String mimeType = null;
    
        // 1. 优先从内容中猜测
        try (InputStream inputStream = resource.getInputStream()) {
            mimeType = URLConnection.guessContentTypeFromStream(inputStream);
        }
    
        // 2. 如果失败，尝试根据扩展名（ServletContext）
        if (mimeType == null && servletContext != null) {
            mimeType = servletContext.getMimeType(resource.getFilename());
        }
    
        // 3. 如果还是失败，尝试 Files.probeContentType
        if (mimeType == null) {
            try {
                mimeType = Files.probeContentType(resource.getFile().toPath());
            } catch (IOException | UnsupportedOperationException ignored) {}
        }
    
        return mimeType != null ? mimeType : "application/octet-stream";
    }
}

