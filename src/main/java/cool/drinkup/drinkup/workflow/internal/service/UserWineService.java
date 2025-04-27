package cool.drinkup.drinkup.workflow.internal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import cool.drinkup.drinkup.user.spi.AuthenticationServiceFacade;
import cool.drinkup.drinkup.user.spi.AuthenticatedUserDTO;
import cool.drinkup.drinkup.workflow.internal.controller.resp.WorkflowBartenderChatResp;
import cool.drinkup.drinkup.workflow.internal.mapper.UserWineMapper;
import cool.drinkup.drinkup.workflow.internal.model.UserWine;
import cool.drinkup.drinkup.workflow.internal.repository.UserWineRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserWineService {
    private final UserWineRepository userWineRepository;
    private final UserWineMapper userWineMapper;
    private final AuthenticationServiceFacade authenticationServiceFacade;

    @Transactional
    public void saveUserWine(WorkflowBartenderChatResp chatBotResponse) {
        Optional<AuthenticatedUserDTO> currentAuthenticatedUser = authenticationServiceFacade.getCurrentAuthenticatedUser();
        if (currentAuthenticatedUser.isEmpty()) {
            throw new IllegalStateException("Expected authenticated user but got none");
        }
        AuthenticatedUserDTO authenticatedUserDTO = currentAuthenticatedUser.get();
        Long userId = authenticatedUserDTO.userId();
        var userWine = userWineMapper.toUserWine(chatBotResponse, userId);
        userWineRepository.save(userWine);
    }

    public Page<UserWine> getUserWine(PageRequest pageRequest) {
        Optional<AuthenticatedUserDTO> currentAuthenticatedUser = authenticationServiceFacade.getCurrentAuthenticatedUser();
        if (currentAuthenticatedUser.isEmpty()) {
            throw new IllegalStateException("Expected authenticated user but got none");
        }
        AuthenticatedUserDTO authenticatedUserDTO = currentAuthenticatedUser.get();
        Long userId = authenticatedUserDTO.userId();
        return userWineRepository.findByUserId(userId, pageRequest);
    }
}