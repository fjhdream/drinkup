package cool.drinkup.drinkup.wine.spi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import cool.drinkup.drinkup.shared.dto.WorkflowBartenderChatDto;
import cool.drinkup.drinkup.wine.internal.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.wine.internal.model.UserWine;
import jakarta.annotation.Nullable;

public interface UserWineServiceFacade {
    void saveUserWine(WorkflowBartenderChatDto chatBotResponse);
    Page<UserWine> getUserWine(PageRequest pageRequest);
    @Nullable UserWine getRandomUserWine();
    List<UserWine> getRandomUserWines(int count);
    WorkflowUserWineVo toWorkflowUserWineVo(UserWine userWine);
} 