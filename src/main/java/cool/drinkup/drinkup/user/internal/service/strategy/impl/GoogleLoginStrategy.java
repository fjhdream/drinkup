package cool.drinkup.drinkup.user.internal.service.strategy.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import cool.drinkup.drinkup.user.internal.controller.req.LoginRequest;
import cool.drinkup.drinkup.user.internal.model.OAuthTypeEnum;
import cool.drinkup.drinkup.user.internal.model.RoleEnum;
import cool.drinkup.drinkup.user.internal.model.User;
import cool.drinkup.drinkup.user.internal.service.GoogleTokenService;
import cool.drinkup.drinkup.user.internal.service.UserService;
import cool.drinkup.drinkup.user.internal.service.strategy.LoginStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Google 登录策略
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleLoginStrategy implements LoginStrategy {

    private final GoogleTokenService googleTokenService;
    private final UserService userService;

    @Override
    public LoginRequest.LoginType getLoginType() {
        return LoginRequest.LoginType.GOOGLE;
    }

    @Override
    public boolean validateCredentials(LoginRequest loginRequest) {
        String idToken = loginRequest.getIdToken();

        if ( !StringUtils.hasText(idToken)) {
            log.warn("Google ID 令牌为空");
            return false;
        }

        try {
            GoogleIdToken token = googleTokenService.verifyIdToken(idToken);
            return token != null;
        } catch (Exception e) {
            log.error("Google 令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public LoginResult getOrCreateUser(LoginRequest loginRequest) {
        try {
            GoogleIdToken token = googleTokenService.verifyIdToken(loginRequest.getIdToken());
            if ( token == null) {
                throw new RuntimeException("无效的 Google 令牌");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String oauthId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            // 通过邮箱查找现有用户
            Optional<User> user = userService.findByOauthId(oauthId, OAuthTypeEnum.GOOGLE);
            if (user.isEmpty()) {
                User newUser = createNewGoogleUser(email, name, picture, oauthId, OAuthTypeEnum.GOOGLE);
                return new LoginResult(newUser, true);
            }
            return new LoginResult(user.get(), false);

        } catch (Exception e) {
            log.error("获取或创建 Google 用户失败: {}", e.getMessage());
            throw new RuntimeException("Google 登录失败");
        }
    }

    @Override
    public String getUserIdentifier(LoginRequest loginRequest) {
        try {
            GoogleIdToken token = googleTokenService.verifyIdToken(loginRequest.getIdToken());
            if ( token != null) {
                return token.getPayload().getEmail();
            }
        } catch (Exception e) {
            log.error("获取 Google 用户标识符失败: {}", e.getMessage());
        }
        return null;
    }

    private User createNewGoogleUser(String email, String name, String picture, String oauthId,
            OAuthTypeEnum oauthType) {
        log.info("创建新 Google 用户，邮箱: {}", email);

        User user = new User();
        user.setUsername(name); // 使用用户名作为用户名
        user.setNickname(StringUtils.hasText(name) ? name : "Google用户");
        user.setAvatar(picture);
        user.setPassword(userService.generateRandomPassword());
        user.setEmail(email);
        user.setOauthId(oauthId);
        user.setOauthType(oauthType);
        user.setEnabled(true);

        // 设置默认角色
        Set<String> roles = new HashSet<>();
        roles.add(RoleEnum.USER.getRole());
        user.setRoles(roles);

        return userService.save(user);
    }
}