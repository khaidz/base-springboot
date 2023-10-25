package net.khaibq.ecommerce.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.khaibq.ecommerce.entity.User;
import net.khaibq.ecommerce.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("userDetailsService")
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameEqualsIgnoreCase(username);
        if (user != null) {
            List<String> roleList = user.getRoles() == null ? new ArrayList<>() : List.of(user.getRoles().split(","));
            List<GrantedAuthority> grantedAuthorityList = roleList
                    .stream()
                    .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority))
                    .collect(Collectors.toList());

            return new CustomUser(user.getUsername(), user.getPassword(), Objects.equals(0, user.getIsDeleted()) && Objects.equals(1, user.getStatus()),
                    true, true, true , grantedAuthorityList, user.getId(), user.getEmail(), user.getAvatar());
        }
        throw new UsernameNotFoundException("User không tồn tại");
    }
}
