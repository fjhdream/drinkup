package cool.drinkup.drinkup.workflow.controller.req;

import java.util.List;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WorkflowBartenderChatReq {
    private List<WorkflowBartenderChatVo> messages;

    @Data
    public static class WorkflowBartenderChatVo {
        @Pattern(regexp = "^(assistant|user)$", message = "角色只能是 assistant 或 user")
        private String role;
        private String content;
    }
}
