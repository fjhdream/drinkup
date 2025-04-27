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

    @Schema(description = "用户信息")
    private UserProfileResp user;
}
