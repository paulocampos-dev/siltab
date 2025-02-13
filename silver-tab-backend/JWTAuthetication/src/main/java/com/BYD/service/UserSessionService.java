package com.BYD.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.BYD.mapper.UserMapper;
import com.BYD.mapper.UserSessionMapper;
import com.BYD.model.UserSession;
import com.BYD.model.User;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import io.jsonwebtoken.Claims;


@Service
    public class    UserSessionService {
        private final JwtService jwtService;
        private final UserMapper userMapper;
        private final UserSessionMapper userSessionMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserSessionService.class);

    public UserSessionService(UserSessionMapper userSessionMapper, JwtService jwtService, UserMapper userMapper) {
            this.userSessionMapper = userSessionMapper;
            this.jwtService = jwtService;
            this.userMapper = userMapper;
    }

    @Transactional
    public UserSession createUserSession(User user) {
        UserSession session = new UserSession();
        session.setUser(user);
        session.setLoginSessionUuid(UUID.randomUUID().toString());
        session.setSessionVersion(1);

        userSessionMapper.insert(session);
        return session;
    }

    @Transactional
    public UserSession verifyAndRotateSession(String refreshToken) {
        Claims claims = jwtService.getRefreshTokenClaims(refreshToken);
        String loginSessionUuid = claims.get("loginSessionUuid", String.class);
        Integer tokenVersion = claims.get("sessionVersion", Integer.class);
        Long userId = claims.get("userId", Long.class);


        User user = Optional.ofNullable(userMapper.findById(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserSession session = Optional.ofNullable(
                        userSessionMapper.findByUserIdAndUuid(userId, loginSessionUuid))
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Token reuse detected
        if (tokenVersion < session.getSessionVersion()) {
            invalidateSession(session);
            throw new SecurityException("Token reuse detected");
        }

        // Rotate session version
        session.setSessionVersion(session.getSessionVersion() + 1);
        userSessionMapper.update(session);
        return session;
    }

    @Transactional
    public void invalidateSession(UserSession session) {
        userSessionMapper.deleteByUserIdAndUuid(
                session.getUser().getId(),
                session.getLoginSessionUuid()
        );
    }

    @Transactional
    public void invalidateAllUserSessions(User user) {
        userSessionMapper.deleteByUserId(user.getId());
    }
}