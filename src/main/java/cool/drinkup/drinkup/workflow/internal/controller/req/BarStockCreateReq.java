package cool.drinkup.drinkup.workflow.internal.controller.req;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

import lombok.Data;

@Data
public class BarStockCreateReq {
    @JsonAlias({"bar_stocks", "user_stock"})
    private List<InnerBarStockCreateReq> barStocks;

    @Data
    public static class InnerBarStockCreateReq {
        private String name;
        private String type;
        private String iconType;
        private String description;
    
    } 
}
