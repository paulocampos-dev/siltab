package com.BYD.controller;

import com.BYD.dto.user.UserTokenResponse;
import com.BYD.mapper.UserProfileMapper;
import com.BYD.model.User;
import com.BYD.model.UserSession;
import com.BYD.service.AuthService;
import com.BYD.service.JwtService;
import com.BYD.service.UserSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserSessionService userSessionService;
    private final UserProfileMapper userProfileMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService,
                          JwtService jwtService,
                          UserSessionService userSessionService,
                          UserProfileMapper userProfileMapper) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userSessionService = userSessionService;
        this.userProfileMapper = userProfileMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            UserTokenResponse authResponse = authService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            // Create new session
            UserSession session = userSessionService.createUserSession(authResponse.getUser());

            // Generate tokens
            String accessToken = jwtService.generateToken(authResponse.getUser());
            String refreshToken = jwtService.generateRefreshToken(authResponse.getUser(), session);

//            // Generate tokens and get their expiration
//            TokenWithExpiration accessTokenInfo = jwtService.generateTokenWithExpiration(authResponse.getUser());
//            TokenWithExpiration refreshTokenInfo = jwtService.generateRefreshTokenWithExpiration(authResponse.getUser(), session);


//            // Set cookies with matching expiration
//            Cookie accessCookie = new Cookie("accessToken", accessTokenInfo.getToken());
//            accessCookie.setHttpOnly(true);
//            accessCookie.setSecure(true);
//            accessCookie.setMaxAge((int)(accessTokenInfo.getExpirationTime() / 1000)); // Convert ms to seconds
//            accessCookie.setPath("/");
//
//            Cookie refreshCookie = new Cookie("refreshToken", refreshTokenInfo.getToken());
//            refreshCookie.setHttpOnly(true);
//            refreshCookie.setSecure(true);
//            refreshCookie.setMaxAge((int)(refreshTokenInfo.getExpirationTime() / 1000));
//            refreshCookie.setPath("/auth/refresh");
//
//            response.addCookie(accessCookie);
//            response.addCookie(refreshCookie);


            String roleName = userProfileMapper.getRoleNameById(authResponse.getUser().getRole());
            String positionName = userProfileMapper.getPositionNameById(authResponse.getUser().getPositionId());
            logger.debug("Role name and position name: {}, {}",roleName,positionName);

            String userEntityAuthority = userProfileMapper.getUserEntityAuthority(
                    authResponse.getUser().getId(),
                    authResponse.getUser().getPositionId()
            );

            String hasCommercialPolicy = userProfileMapper.checkUserCommercialPolicyAccess(
                    authResponse.getUser().getId(),
                    authResponse.getUser().getPositionId()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("username", authResponse.getUser().getUsername());
            response.put("email", authResponse.getUser().getEmail());
            response.put("role", authResponse.getUser().getRole());
            response.put("roleName", roleName);
            response.put("position", authResponse.getUser().getPositionId());
            response.put("positionName", positionName);
            response.put("id", authResponse.getUser().getId());
            response.put("userEntityAuthority", userEntityAuthority);
            response.put("userHasAccessToCommercialPolicy", hasCommercialPolicy);
            response.put("tokenType", "Bearer");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Login failed with error: ", e);
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String requestRefreshToken = request.get("refreshToken");

        try {
            // Verify and rotate session
            UserSession rotatedSession = userSessionService.verifyAndRotateSession(requestRefreshToken);

            // Generate new tokens
            String newAccessToken = jwtService.generateToken(rotatedSession.getUser());
            String newRefreshToken = jwtService.generateRefreshToken(rotatedSession.getUser(), rotatedSession);

            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken,
                    "tokenType", "Bearer"
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Security violation detected. Please login again."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // is it still working after changing refresh token structure?
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean isValid = jwtService.validateToken(token);
            if (isValid) {
                String username = jwtService.getUsernameFromToken(token);
                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", username
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "valid", false,
                        "error", "Invalid token"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "error", "Invalid token format"
            ));
        }
    }
}