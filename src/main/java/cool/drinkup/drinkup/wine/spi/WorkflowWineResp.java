package cool.drinkup.drinkup.wine.spi;

import java.util.List;

import lombok.Data;

@Data
public class WorkflowWineResp {
    private List<WorkflowWineVo> wines;
} 