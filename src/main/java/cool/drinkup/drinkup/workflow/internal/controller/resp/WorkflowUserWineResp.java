package cool.drinkup.drinkup.workflow.internal.controller.resp;

import java.util.List;

import lombok.Data;

@Data
public class WorkflowUserWineResp {
    private List<WorkflowUserWineVo> wines;
}
