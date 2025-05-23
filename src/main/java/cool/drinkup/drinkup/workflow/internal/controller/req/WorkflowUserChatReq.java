package cool.drinkup.drinkup.workflow.internal.controller.req;

import java.util.List;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WorkflowUserChatReq {
    private List<WorkflowUserChatVo> messages;
    private List<Long> barIds;
    private String imageId;

    @Data
    public static class WorkflowUserChatVo {
        @Pattern(regexp = "^(assistant|user)$", message = "角色只能是 assistant 或 user")
        private String role;
        private String content;
    }
}
