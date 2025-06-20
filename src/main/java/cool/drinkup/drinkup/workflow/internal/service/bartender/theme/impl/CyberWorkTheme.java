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
public class CyberWorkTheme implements Theme {

    private final String name = "cyberWork";

    private final ResourceLoader resourceLoader;

    private String theme;

    @PostConstruct
    public void init() {
        Resource themeResource = resourceLoader.getResource("classpath:themes/" + this.name + ".txt");
        try {
            this.theme = new String(themeResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Read theme failed!", e);
        }
    }
    
    @Override
    public String getName() {
        return this.theme;
    }

}
