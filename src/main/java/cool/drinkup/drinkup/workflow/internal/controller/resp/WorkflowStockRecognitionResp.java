package cool.drinkup.drinkup.workflow.internal.controller.resp;

import java.util.List;

import cool.drinkup.drinkup.workflow.internal.model.BarStock;
import lombok.Data;

@Data
public class WorkflowStockRecognitionResp {
    private Long barId;
    private List<BarStock> recognizedStocks;
}