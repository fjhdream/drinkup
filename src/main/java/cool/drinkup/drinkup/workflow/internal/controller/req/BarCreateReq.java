package cool.drinkup.drinkup.workflow.internal.controller.req;

import lombok.Data;

@Data
public class BarCreateReq {
    private String name;
    private String userId;
    private String description;
}
