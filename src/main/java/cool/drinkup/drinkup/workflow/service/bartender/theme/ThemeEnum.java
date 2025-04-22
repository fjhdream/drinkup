package cool.drinkup.drinkup.workflow.service.bartender.theme;

public enum ThemeEnum {
    PHILOSOPHY,
    CYBER_WORK,
    ;

    public static ThemeEnum fromValue(String value) {
        return ThemeEnum.valueOf(value.toUpperCase());
    }
}
