package cool.drinkup.drinkup.user.internal.service;

import cool.drinkup.drinkup.user.internal.mapper.UserMapper;
import cool.drinkup.drinkup.user.internal.model.DrinkupUserDetails;
import cool.drinkup.drinkup.user.internal.model.User;
import cool.drinkup.drinkup.user.spi.AuthenticatedUserDTO;
import cool.drinkup.drinkup.user.spi.AuthenticationServiceFacade;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Primary
@Service
@RequiredArgsConstructor
public class DrinkupUserDetailsService implements UserDetailsService, AuthenticationServiceFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new DrinkupUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                true,
                user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    @Override
    public Optional<AuthenticatedUserDTO> getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!(userDetails instanceof DrinkupUserDetails drinkupUserDetails)) {
            return Optional.empty();
        }
        return Optional.of(userMapper.toAuthenticatedUserDTO(drinkupUserDetails));
    }
}
