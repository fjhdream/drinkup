package cool.drinkup.drinkup.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cool.drinkup.drinkup.user.controller.resp.UserProfileResp;
import cool.drinkup.drinkup.user.mapper.UserMapper;
import cool.drinkup.drinkup.user.repository.UserRepository;
import cool.drinkup.drinkup.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理相关的接口")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Operation(summary = "获取个人资料", description = "获取当前登录用户的个人资料信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("获取个人资料: {}", username);
        return userService.findByUsername(username)
                .map(user -> {
                    UserProfileResp userProfileResp = userMapper.toUserProfileResp(user);
                    return ResponseEntity.ok(userProfileResp);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "获取所有用户", description = "管理员获取系统中所有用户列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "权限不足，需要管理员权限")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("email", user.getEmail());
                    userMap.put("enabled", user.isEnabled());
                    userMap.put("roles", user.getRoles());
                    return userMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }
    
    @Operation(summary = "获取指定用户", description = "管理员获取指定ID的用户信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "403", description = "权限不足，需要管理员权限")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "用户ID") 
            @PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("username", user.getUsername());
                    userMap.put("email", user.getEmail());
                    userMap.put("enabled", user.isEnabled());
                    userMap.put("roles", user.getRoles());
                    return ResponseEntity.ok(userMap);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "更新用户状态", description = "管理员启用或禁用用户账户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "403", description = "权限不足，需要管理员权限")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserStatus(
            @Parameter(description = "用户ID") 
            @PathVariable Long id,
            @Parameter(description = "用户状态信息，包含enabled字段") 
            @RequestBody Map<String, Boolean> request) {
        
        Boolean enabled = request.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().body("请提供是否启用的状态");
        }
        
        return userRepository.findById(id)
                .map(user -> {
                    user.setEnabled(enabled);
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of(
                            "message", "用户状态已更新",
                            "username", user.getUsername(),
                            "enabled", user.isEnabled()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 