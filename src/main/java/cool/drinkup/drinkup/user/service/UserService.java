package cool.drinkup.drinkup.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import cool.drinkup.drinkup.user.controller.req.UserRegisterReq;
import cool.drinkup.drinkup.user.mapper.UserMapper;
import cool.drinkup.drinkup.user.model.RoleEnum;
import cool.drinkup.drinkup.user.model.User;
import cool.drinkup.drinkup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Transactional
    public User registerUser(UserRegisterReq registerReq) {
        User user = userMapper.toUser(registerReq);
        user.setUsername(randomUserName());
        // 先保存用户以获取ID
        user = userRepository.save(user);
        user.setNickname("品鉴师" + String.format("%06d", user.getId()));
        // 设置默认角色（如果没有提供）
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<String> roles = new HashSet<>();
            roles.add(RoleEnum.USER.getRole());
            user.setRoles(roles);
        }
        return userRepository.save(user);
    }

    private String randomUserName() {
        // 如果用户名为空，生成一个16位随机字母作为用户名
        StringBuilder randomUsername = new StringBuilder();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 16; i++) {
            randomUsername.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return randomUsername.toString();
    }

    @Transactional
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
        
        Set<String> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
            user.setRoles(roles);
        }
        
        roles.add(roleName);
        userRepository.save(user);
    }
    
    @Transactional
    public void removeRoleFromUser(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + username));
        
        Set<String> roles = user.getRoles();
        if (roles != null) {
            roles.remove(roleName);
            userRepository.save(user);
        }
    }
    
    @Transactional(readOnly = true)
    public boolean hasRole(String username, String roleName) {
        return userRepository.findByUsername(username)
                .map(user -> user.getRoles() != null && user.getRoles().contains(roleName))
                .orElse(false);
    }
} 