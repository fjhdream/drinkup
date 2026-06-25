package cool.drinkup.drinkup.display.internal.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DisplayType {
    POPUP("POPUP", "提示弹窗"),
    BANNER("BANNER", "横幅广告");

    private final String code;
    private final String description;
}
