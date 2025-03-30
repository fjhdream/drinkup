package cool.drinkup.drinkup.user.mapper;

import org.mapstruct.Mapper;

import cool.drinkup.drinkup.user.controller.req.UserRegisterReq;
import cool.drinkup.drinkup.user.controller.resp.UserProfileResp;
import cool.drinkup.drinkup.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRegisterReq registerReq);
    UserProfileResp toUserProfileResp(User user);
}