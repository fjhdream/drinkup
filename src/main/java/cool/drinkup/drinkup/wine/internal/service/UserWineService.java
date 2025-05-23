package cool.drinkup.drinkup.wine.internal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import cool.drinkup.drinkup.shared.dto.WorkflowBartenderChatDto;
import cool.drinkup.drinkup.user.spi.AuthenticatedUserDTO;
import cool.drinkup.drinkup.user.spi.AuthenticationServiceFacade;
import cool.drinkup.drinkup.wine.internal.controller.resp.WorkflowUserWineVo;
import cool.drinkup.drinkup.wine.internal.mapper.UserWineMapper;
import cool.drinkup.drinkup.wine.internal.model.UserWine;
import cool.drinkup.drinkup.wine.internal.repository.UserWineRepository;
import cool.drinkup.drinkup.wine.spi.UserWineServiceFacade;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserWineService implements UserWineServiceFacade {
    private final UserWineRepository userWineRepository;
    private final UserWineMapper userWineMapper;
    private final AuthenticationServiceFacade authenticationServiceFacade;

    @Override
    @Transactional
    public void saveUserWine(WorkflowBartenderChatDto chatBotResponse) {
        Optional<AuthenticatedUserDTO> currentAuthenticatedUser = authenticationServiceFacade.getCurrentAuthenticatedUser();
        if (currentAuthenticatedUser.isEmpty()) {
            throw new IllegalStateException("Expected authenticated user but got none");
        }
        AuthenticatedUserDTO authenticatedUserDTO = currentAuthenticatedUser.get();
        Long userId = authenticatedUserDTO.userId();
        var userWine = userWineMapper.toUserWine(chatBotResponse, userId);
        userWineRepository.save(userWine);
    }

    @Override
    public Page<UserWine> getUserWine(PageRequest pageRequest) {
        Optional<AuthenticatedUserDTO> currentAuthenticatedUser = authenticationServiceFacade.getCurrentAuthenticatedUser();
        if (currentAuthenticatedUser.isEmpty()) {
            throw new IllegalStateException("Expected authenticated user but got none");
        }
        AuthenticatedUserDTO authenticatedUserDTO = currentAuthenticatedUser.get();
        Long userId = authenticatedUserDTO.userId();
        return userWineRepository.findByUserId(userId, pageRequest);
    }

    @Override
    public @Nullable UserWine getRandomUserWine() {
        Optional<AuthenticatedUserDTO> currentAuthenticatedUser = authenticationServiceFacade.getCurrentAuthenticatedUser();
        if (currentAuthenticatedUser.isEmpty()) {
            throw new IllegalStateException("Expected authenticated user but got none");
        }
        AuthenticatedUserDTO authenticatedUserDTO = currentAuthenticatedUser.get();
        Long userId = authenticatedUserDTO.userId();
        return userWineRepository.findRandomUserWine(userId);
    }

    @Override
    public WorkflowUserWineVo toWorkflowUserWineVo(UserWine userWine) {
        return userWineMapper.toWorkflowUserWineVo(userWine);
    }
} 