package cool.drinkup.drinkup.wine.internal.spi.impl;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cool.drinkup.drinkup.favorite.spi.FavoriteObjectLoader;
import cool.drinkup.drinkup.favorite.spi.FavoriteType;
import cool.drinkup.drinkup.wine.internal.model.Wine;
import cool.drinkup.drinkup.wine.internal.repository.WineRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WineFavoriteLoader implements FavoriteObjectLoader<Wine> {
    
    private final WineRepository wineRepository;
    
    @Override
    public Map<Long, Wine> loadObjects(List<Long> objectIds) {
        return wineRepository.findAllById(objectIds).stream()
                .collect(Collectors.toMap(Wine::getId, wine -> wine));
    }
    
    @Override
    public boolean validateObject(Long objectId) {
        return wineRepository.existsById(objectId);
    }
    
    @Override
    public void afterFavorite(Long objectId, boolean isFavorite) {
        // 更新收藏计数
        wineRepository.findById(objectId).ifPresent(wine -> {
            wine.setFavoriteCount(wine.getFavoriteCount() + (isFavorite ? 1 : -1));
            wineRepository.save(wine);
        });
    }

    @Override
    public FavoriteType getFavoriteType() {
        return FavoriteType.WINE;
    }
} 