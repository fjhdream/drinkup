package cool.drinkup.drinkup.workflow.controller.resp;

import java.util.List;

import lombok.Data;

@Data
public class WorkflowUserWIneResp {
    private List<WorkflowUserWineVo> wines;
}
