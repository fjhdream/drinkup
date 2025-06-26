package cool.drinkup.drinkup.favorite.spi;

import java.util.List;
import java.util.Map;

/**
 * 收藏对象加载器接口
 */
public interface FavoriteObjectLoader<T> {
    /**
     * 批量加载对象
     * @param objectIds 对象ID列表
     * @return 对象Map，key为对象ID，value为对象实例
     */
    Map<Long, T> loadObjects(List<Long> objectIds);

    /**
     * 验证对象是否存在
     * @param objectId 对象ID
     * @return 是否存在
     */
    boolean validateObject(Long objectId);

    /**
     * 收藏状态变更后的处理
     * @param objectId 对象ID
     * @param isFavorite 是否收藏
     */
    void afterFavorite(Long objectId, boolean isFavorite);

    FavoriteType getFavoriteType();
}
