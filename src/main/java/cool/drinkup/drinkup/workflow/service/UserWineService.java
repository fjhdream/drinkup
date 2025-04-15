package cool.drinkup.drinkup.workflow.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cool.drinkup.drinkup.user.model.DrinkupUserDetails;
import cool.drinkup.drinkup.workflow.controller.resp.WorkflowBartenderChatResp;
import cool.drinkup.drinkup.workflow.mapper.UserWineMapper;
import cool.drinkup.drinkup.workflow.model.UserWine;
import cool.drinkup.drinkup.workflow.repository.UserWineRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserWineService {
    private final UserWineRepository userWineRepository;
    private final UserWineMapper userWineMapper;

    @Transactional
    public void saveUserWine(WorkflowBartenderChatResp chatBotResponse) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!(userDetails instanceof DrinkupUserDetails)) {
            throw new IllegalStateException("Expected DrinkupUserDetails but got " + userDetails.getClass());
        }
        Long userId = ((DrinkupUserDetails) userDetails).getId();
        var userWine = userWineMapper.toUserWine(chatBotResponse, userId);
        userWineRepository.save(userWine);
    }

    public Page<UserWine> getUserWine(PageRequest pageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!(userDetails instanceof DrinkupUserDetails)) {
            throw new IllegalStateException("Expected DrinkupUserDetails but got " + userDetails.getClass());
        }
        Long userId = ((DrinkupUserDetails) userDetails).getId();
        return userWineRepository.findByUserId(userId, pageRequest);
    }
}