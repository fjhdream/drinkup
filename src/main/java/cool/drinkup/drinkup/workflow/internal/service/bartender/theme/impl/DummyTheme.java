package cool.drinkup.drinkup.workflow.internal.service.bartender.theme.impl;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cool.drinkup.drinkup.workflow.internal.service.bartender.theme.Theme;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DummyTheme implements Theme {

    private final String name = "dummy";

    private final ResourceLoader resourceLoader;

    private String theme;

    @PostConstruct
    public void init() {
        this.theme = "随意发挥你的创意来进行对用户的请求来进行发挥, 尽可能匹配用户的输入文本的情绪";
    }
    
    @Override
    public String getName() {
        return this.theme;
    }

}
