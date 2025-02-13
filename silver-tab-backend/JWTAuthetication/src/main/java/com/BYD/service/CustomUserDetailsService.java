package com.BYD.service;

import com.BYD.model.User;
import com.BYD.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;

    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = Optional.ofNullable(userMapper.findByUsername(username))
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found with username: " + username));

            logger.debug("Retrieved user from database: {}", user.getUsername());
            logger.debug("Retrieved password from database: {}", user.getPassword());
            logger.debug("Retrieved role from database: {}", user.getRole());

            // Just pass the numeric role as the authority
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

            logger.info("Successfully loaded user: {} with role: {}",
                    username, user.getRole());

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );

        } catch (UsernameNotFoundException e) {
            logger.error("User not found: {}", username);
            throw e;
        } catch (Exception e) {
            logger.error("Error loading user {}: {}", username, e.getMessage());
            throw new RuntimeException("Error loading user", e);
        }
    }
}