package cool.drinkup.drinkup.workflow.mapper;

import org.mapstruct.Mapper;

import java.util.List;

import cool.drinkup.drinkup.workflow.controller.req.BarStockCreateReq;
import cool.drinkup.drinkup.workflow.model.BarStock;

@Mapper(componentModel = "spring")
public interface BatStockMapper {
    
    default List<BarStock> toBarStock(BarStockCreateReq barStockCreateReq, Long barId) {
        if (barStockCreateReq == null) {
            return null;
        }
        
        List<BarStock> barStocks = new java.util.ArrayList<>();
        
        barStockCreateReq.getBarStocks().forEach(item -> {
            BarStock barStock = new BarStock();
            barStock.setBarId(barId);
            barStock.setName(item.getName());
            barStock.setType(item.getType());
            barStock.setIconType(item.getIconType());
            barStock.setDescription(item.getDescription());
            barStocks.add(barStock);
        });
        
        return barStocks;
    }
}
