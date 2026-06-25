package cool.drinkup.drinkup.display.internal.mapper;

import cool.drinkup.drinkup.display.internal.enums.DisplayType;
import cool.drinkup.drinkup.display.internal.model.DisplayItem;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayItemRepository extends JpaRepository<DisplayItem, Long> {

    @Query("SELECT d FROM DisplayItem d WHERE d.isActive = true "
            + "AND (d.startTime IS NULL OR d.startTime <= :now) "
            + "AND (d.endTime IS NULL OR d.endTime >= :now) "
            + "ORDER BY d.priority DESC, d.createdTime DESC")
    List<DisplayItem> findActiveItems(@Param("now") ZonedDateTime now);

    @Query("SELECT d FROM DisplayItem d WHERE d.isActive = true "
            + "AND d.type = :type "
            + "AND (d.startTime IS NULL OR d.startTime <= :now) "
            + "AND (d.endTime IS NULL OR d.endTime >= :now) "
            + "ORDER BY d.priority DESC, d.createdTime DESC")
    List<DisplayItem> findActiveItemsByType(@Param("type") DisplayType type, @Param("now") ZonedDateTime now);
}
