package cool.drinkup.drinkup.wine.spi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import cool.drinkup.drinkup.wine.internal.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.wine.internal.model.UserWine;
import cool.drinkup.drinkup.workflow.spi.WorkflowBartenderChatDto;
import jakarta.annotation.Nullable;

public interface UserWineServiceFacade {
    void saveUserWine(WorkflowBartenderChatDto chatBotResponse);
    Page<UserWine> getUserWine(PageRequest pageRequest);
    @Nullable UserWine getRandomUserWine();
    WorkflowUserWineVo toWorkflowUserWineVo(UserWine userWine);
} 