package cool.drinkup.drinkup.workflow.service.stock;

import org.springframework.stereotype.Service;

import java.util.List;

import cool.drinkup.drinkup.workflow.controller.req.BarStockCreateReq;
import cool.drinkup.drinkup.workflow.mapper.BatStockMapper;
import cool.drinkup.drinkup.workflow.model.BarStock;
import cool.drinkup.drinkup.workflow.repository.BarStockRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BarStockService {
    
    private final BarStockRepository barStockRepository;

    private final BatStockMapper batStockMapper;
    
    public List<BarStock> getBarStock(Long barId) {
        return barStockRepository.findByBarId(barId);
    }

    public List<BarStock> createBarStock(Long barId, BarStockCreateReq barStockCreateReq) {
        List<BarStock> barStocks = batStockMapper.toBarStock(barStockCreateReq, barId);
        return barStockRepository.saveAll(barStocks);
    }
}
