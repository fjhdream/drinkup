package cool.drinkup.drinkup.workflow.controller.req;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class WorkflowStockRecognitionReq {
    private Long barId;
    private MultipartFile image;
}