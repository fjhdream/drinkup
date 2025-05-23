package cool.drinkup.drinkup.workflow.internal.mapper;

import org.mapstruct.Mapper;

import java.util.List;

import cool.drinkup.drinkup.workflow.internal.controller.req.BarStockCreateReq;
import cool.drinkup.drinkup.workflow.internal.model.Bar;
import cool.drinkup.drinkup.workflow.internal.model.BarStock;

@Mapper(componentModel = "spring")
public interface BatStockMapper {
    
    default List<BarStock> toBarStock(BarStockCreateReq barStockCreateReq, Long barId) {
        if (barStockCreateReq == null) {
            return null;
        }
        
        List<BarStock> barStocks = new java.util.ArrayList<>();
        
        barStockCreateReq.getBarStocks().forEach(item -> {
            BarStock barStock = new BarStock();
            Bar bar = new Bar();
            bar.setId(barId);
            barStock.setBar(bar);
            barStock.setName(item.getName());
            barStock.setType(item.getType());
            barStock.setIconType(item.getIconType());
            barStock.setDescription(item.getDescription());
            barStocks.add(barStock);
        });
        
        return barStocks;
    }
}
