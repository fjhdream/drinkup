package cool.drinkup.drinkup.workflow.internal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cool.drinkup.drinkup.workflow.internal.model.Wine;
import cool.drinkup.drinkup.workflow.internal.repository.WineRepository;
import jakarta.annotation.Nullable;

@Service
public class WineService {
    
    @Autowired
    private WineRepository wineRepository;

    public @Nullable Wine getWineById(Long id) {
        return wineRepository.findById(id).orElse(null);
    }

    public Page<Wine> getWinesByTag(String tagMainBaseSpirit, Pageable pageable) {
        if (!StringUtils.hasText(tagMainBaseSpirit)) {
            return wineRepository.findAll(pageable);
        }
        return wineRepository.findByTagMainBaseSpirit(tagMainBaseSpirit, pageable);
    }
}
