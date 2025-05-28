package cool.drinkup.drinkup.wine.spi;

import java.util.List;

import cool.drinkup.drinkup.wine.internal.controller.resp.WorkflowWineVo;
import lombok.Data;

@Data
public class WorkflowWineResp {
    private List<WorkflowWineVo> wines;
} 