package cool.drinkup.drinkup.display.internal.controller.resp;

import cool.drinkup.drinkup.display.internal.enums.DisplayType;
import lombok.Data;

@Data
public class DisplayItemVo {
    private DisplayType type;
    private String imageUrl;
    private String linkUrl;
    private String content;
}
