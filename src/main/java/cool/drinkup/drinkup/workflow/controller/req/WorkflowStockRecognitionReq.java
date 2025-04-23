package cool.drinkup.drinkup.workflow.controller.req;

import lombok.Data;

@Data
public class WorkflowStockRecognitionReq {
    private Long barId;
    private String imageId;
}