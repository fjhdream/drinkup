package cool.drinkup.drinkup.user.internal.controller.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户登录响应")
public class UserLoginResp {

    @Schema(description = "登录消息")
    private String message;

    @Schema(description = "是否新用户")
    private Boolean isNewUser;

    @Schema(description = "用户信息")
    private UserProfileResp user;

    @Schema(description = "Session ID（Base64 编码，前端保存到 Keychain，后续请求通过 Authorization: Bearer 发送）")
    private String sessionId;
}
