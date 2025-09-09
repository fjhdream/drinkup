package cool.drinkup.drinkup.display.api;

import cool.drinkup.drinkup.display.internal.controller.resp.DisplayItemVo;
import cool.drinkup.drinkup.display.internal.enums.DisplayType;
import java.util.List;

public interface DisplayItemService {

    List<DisplayItemVo> getActiveDisplayItems();

    List<DisplayItemVo> getActiveDisplayItemsByType(DisplayType type);
}
