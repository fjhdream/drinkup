package cool.drinkup.drinkup.workflow.controller.req;

import java.util.List;

import lombok.Data;

@Data
public class BarStockCreateReq {
    private List<InnerBarStockCreateReq> barStocks;

    public static record InnerBarStockCreateReq(
        String name,
        String type
    ) {
    }
}
