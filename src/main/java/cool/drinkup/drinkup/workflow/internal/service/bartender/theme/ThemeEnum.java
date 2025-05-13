package cool.drinkup.drinkup.workflow.internal.service.bartender.theme;

import jakarta.annotation.Nullable;

public enum ThemeEnum {
    PHILOSOPHY,
    CYBER_WORK,
    MOVIE,
    RANDOM,
    ;

    public static @Nullable ThemeEnum fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null; // 返回null表示没有主题
        }
        return ThemeEnum.valueOf(value.toUpperCase());
    }
}
