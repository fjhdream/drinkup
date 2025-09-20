package cool.drinkup.drinkup.common.log.trace.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "埋点请求数据")
public class TraceRequest {

    @NotBlank(message = "Type不能为空")
    @Pattern(regexp = "^[A-Z][A-Z_]*$", message = "Type必须为大写字母和下划线组成")
    @Schema(description = "事件类型（大写字母和下划线）", example = "USER_ACTION")
    private String type;

    @NotBlank(message = "SubType不能为空")
    @Pattern(regexp = "^[A-Z][A-Z_]*$", message = "SubType必须为大写字母和下划线组成")
    @Schema(description = "事件子类型（大写字母和下划线）", example = "LOGIN_SUCCESS")
    private String subType;

    @NotBlank(message = "BizNo不能为空")
    @Schema(description = "业务编号", example = "ORDER_20231201_001")
    private String bizNo;

    @Schema(description = "扩展属性")
    private Map<String, Object> properties;
}
