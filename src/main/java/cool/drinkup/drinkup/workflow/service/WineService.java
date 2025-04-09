package cool.drinkup.drinkup.workflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cool.drinkup.drinkup.workflow.model.Wine;
import cool.drinkup.drinkup.workflow.repository.WineRepository;
import jakarta.annotation.Nullable;

@Service
public class WineService {
    
    @Autowired
    private WineRepository wineRepository;

    public @Nullable Wine getWineById(Long id) {
        return wineRepository.findById(id).orElse(null);
    }
}
