package com.BYD.service;

import com.BYD.dto.user.UserTokenResponse;
import com.BYD.mapper.UserMapper;
import com.BYD.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserMapper userMapper,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;

    }

    public UserTokenResponse authenticateUser(String username, String password) {
        try {


            logger.debug("Starting authentication for username: {} with password length: {}",
                    username, password != null ? password.length() : 0);

            User user = Optional.ofNullable(userMapper.findByUsername(username))
                    .orElseThrow(() -> {
                        logger.error("User not found in database: {}", username);
                        return new RuntimeException("User not found");
                    });

            logger.info("User found in database: {}", username);
            logger.info("Attempting to authenticate with provided password");

            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );
                logger.info("Authentication successful");

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtService.generateToken(user);

                return new UserTokenResponse(token, user);

            } catch (BadCredentialsException e) {
                logger.error("Password authentication failed for user: {}", username);
                throw e;
            }

        } catch (Exception e) {
            logger.error("Authentication failed with error: {}", e.getMessage(), e);
            throw e;
        }
    }
}

