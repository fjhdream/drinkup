package cool.drinkup.drinkup.user.mapper;

import org.mapstruct.Mapper;

import cool.drinkup.drinkup.user.controller.req.LoginRequest;
import cool.drinkup.drinkup.user.controller.resp.UserProfileResp;
import cool.drinkup.drinkup.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(LoginRequest loginRequest);
    UserProfileResp toUserProfileResp(User user);
}