package cool.drinkup.drinkup;

import org.junit.Test;
import org.springframework.modulith.core.ApplicationModules;

public class DrinkupApplicationTest {

    @Test
    public void testModuleStructure() {
        ApplicationModules modules = ApplicationModules.of(DrinkupApplication.class);
        modules.verify(); // 自动验证模块边界
    }

    @Test
    public void createApplicationModuleModel() {
        ApplicationModules modules = ApplicationModules.of(DrinkupApplication.class);
        modules.forEach(System.out::println);
    }
}