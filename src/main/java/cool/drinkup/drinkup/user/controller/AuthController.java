package cool.drinkup.drinkup.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import cool.drinkup.drinkup.external.sms.SmsSender;
import cool.drinkup.drinkup.user.controller.req.LoginRequest;
import cool.drinkup.drinkup.user.controller.req.PhoneNumberRequest;
import cool.drinkup.drinkup.user.controller.resp.CommonResp;
import cool.drinkup.drinkup.user.controller.resp.LogoutResp;
import cool.drinkup.drinkup.user.controller.resp.UserLoginResp;
import cool.drinkup.drinkup.user.mapper.UserMapper;
import cool.drinkup.drinkup.user.model.User;
import cool.drinkup.drinkup.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关的接口，包括注册、登录和登出")
public class AuthController {

    private final UserService userService;
    private final SmsSender smsSender;
    private final UserMapper userMapper;

    @Operation(summary = "发送验证码", description = "发送验证码到指定手机号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证码发送成功"),
            @ApiResponse(responseCode = "400", description = "验证码发送失败，可能是手机号格式错误或发送次数限制")
    })
    @PostMapping("/send-verification-code")
    public ResponseEntity<CommonResp<String>> sendVerificationCode(
            @Parameter(description = "手机号请求体") @Valid @RequestBody PhoneNumberRequest request) {
        try {
            smsSender.sendSms(request.getPhoneNumber(), generateVerificationCode());
            return ResponseEntity.ok(CommonResp.success("验证码发送成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResp.error("验证码发送失败: " + e.getMessage()));
        }
    }

    private String generateVerificationCode() {
        // 使用 SecureRandom 生成更安全的6位随机数字验证码
        java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        int code = 100000 + secureRandom.nextInt(900000); // 确保是6位数字
        return String.valueOf(code);
    }

    @Operation(summary = "用户登录", description = "用户使用手机号和验证码登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "登录失败，验证码错误或已过期"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping("/login")
    public ResponseEntity<CommonResp<UserLoginResp>> login(
            @Parameter(description = "登录信息，包含手机号和验证码") @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        String phoneNumber = loginRequest.getPhone();
        String verificationCode = loginRequest.getVerificationCode();

        try {
            // TODO: 暂时关闭短信验证码服务
            // 验证验证码
            // if (!smsSender.verifySms(phoneNumber, verificationCode)) {
            //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            //             .body("验证码错误或已过期");
            // }

            // 查找用户
            User user = userService.findByPhone(phoneNumber).orElse(null);
            
            if (user == null) {
                user = userService.registerUser(loginRequest);
            }

            // 创建 DrinkupUserDetails
            cool.drinkup.drinkup.user.model.DrinkupUserDetails userDetails = new cool.drinkup.drinkup.user.model.DrinkupUserDetails(
                    user.getId(),
                    user.getUsername(),
                    "", // empty password as we're using verification code
                    true,
                    user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList())
            );

            // 创建认证信息
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            // 创建会话
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            UserLoginResp loginResp = UserLoginResp.builder()
                    .message("登录成功")
                    .user(userMapper.toUserProfileResp(user))
                    .build();

            return ResponseEntity.ok(CommonResp.success(loginResp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResp.error("登录失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "用户登出", description = "用户登出并清除会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登出成功")
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonResp<LogoutResp>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if ( session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        LogoutResp logoutResp = LogoutResp.builder()
                .message("Logged out successfully")
                .status("success")
                .build();

        return ResponseEntity.ok(CommonResp.success(logoutResp));
    }
}