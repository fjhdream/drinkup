package cool.drinkup.drinkup.workflow.internal.controller.req;

import lombok.Data;

@Data
public class BarProcurementUpdateReq {
    private String name;
    private String type;
    private String iconType;
    private String description;
} 