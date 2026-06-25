package cool.drinkup.drinkup.workflow.internal.controller.workflow.req.info;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAttachment {
    private String imageId;
    private String imageBase64;
    private String mimeType;

    /**
     * 支持从字符串构造ImageAttachment对象（兼容前端直接发送图片ID/文件名的情况）
     */
    @JsonCreator
    public static ImageAttachment fromString(String imageId) {
        if (imageId == null || imageId.trim().isEmpty()) {
            return null;
        }
        return ImageAttachment.builder().imageId(imageId.trim()).build();
    }

    /**
     * 当对象只包含imageId时，序列化为字符串
     */
    @JsonValue
    public Object toJson() {
        // 如果只有imageId，且没有base64数据和mimeType，则序列化为字符串
        if (imageId != null
                && (imageBase64 == null || imageBase64.isEmpty())
                && (mimeType == null || mimeType.isEmpty())) {
            return imageId;
        }
        // 否则序列化为完整对象
        return this;
    }
}
