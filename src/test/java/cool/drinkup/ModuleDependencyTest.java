package cool.drinkup;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import cool.drinkup.drinkup.DrinkupApplication;

class ModuleDependencyTest {

    @Test
    void verifyModuleDependencies() {
        ApplicationModules modules = ApplicationModules.of(DrinkupApplication.class);
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(DrinkupApplication.class);
        new Documenter(modules)
                .writeDocumentation();
    }
} 