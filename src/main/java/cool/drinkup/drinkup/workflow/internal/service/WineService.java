package cool.drinkup.drinkup.workflow.internal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cool.drinkup.drinkup.workflow.internal.model.Wine;
import cool.drinkup.drinkup.workflow.internal.repository.WineRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WineService {

    private final WineRepository wineRepository;

    public @Nullable Wine getWineById(Long id) {
        return wineRepository.findById(id).orElse(null);
    }

    public Page<Wine> getWinesByTag(String tagMainBaseSpirit, String tagIba,Pageable pageable) {
        return wineRepository.findByTagMainBaseSpiritAndTagIbaWithNullHandling(tagMainBaseSpirit, tagIba, pageable);
    }
}
