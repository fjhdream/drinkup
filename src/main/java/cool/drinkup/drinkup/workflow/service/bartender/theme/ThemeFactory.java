package cool.drinkup.drinkup.workflow.service.bartender.theme;

import org.springframework.stereotype.Component;

import cool.drinkup.drinkup.workflow.service.bartender.theme.impl.CyberWorkTheme;
import cool.drinkup.drinkup.workflow.service.bartender.theme.impl.PhilosophyTheme;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ThemeFactory {

    private final CyberWorkTheme cyberWorkTheme;
    private final PhilosophyTheme philosophyTheme;
    
    public Theme getTheme(ThemeEnum themeEnum) {
        switch (themeEnum) {
            case PHILOSOPHY:
                return philosophyTheme;
            case CYBER_WORK:
                return cyberWorkTheme;
            default:
                throw new IllegalArgumentException("Unsupported theme: " + themeEnum);
        }
    }
}
