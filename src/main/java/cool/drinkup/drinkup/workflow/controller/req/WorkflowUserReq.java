package cool.drinkup.drinkup.workflow.controller.req;

import lombok.Data;

@Data
public class WorkflowUserReq {
    private String userInput;

    public WorkflowUserReq(String userInput) {
        this.userInput = userInput;
    }
}
