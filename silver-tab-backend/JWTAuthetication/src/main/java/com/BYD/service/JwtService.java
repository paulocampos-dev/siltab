package com.BYD.service;

import com.BYD.model.User;
import com.BYD.model.UserSession;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey secretKey;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") Long jwtExpiration,
            @Value("${jwt.refresh-expiration}") Long refreshExpiration
    ) {
        try {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
            this.jwtExpiration = jwtExpiration;
            this.refreshExpiration = refreshExpiration;
            logger.info("JwtService initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing JwtService: {}", e.getMessage());
            throw new IllegalStateException("Could not initialize JwtService", e);
        }
    }

    // Get user ID from token
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("id", Long.class);
        } catch (Exception e) {
            logger.error("Error extracting user ID from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting user ID from token", e);
        }
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", user.getRole());  // Store the role ID directly
        claims.put("email", user.getEmail());
        claims.put("position", user.getPositionId());
        return generateToken(claims, user.getUsername());
    }

    public Integer getRoleFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("role", Integer.class);
        } catch (Exception e) {
            logger.error("Error extracting role from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting role from token", e);
        }
    }

    public Long getPositionFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("position", Long.class);
        } catch (Exception e) {
            logger.error("Error extracting position from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting position from token", e);
        }
    }

    private String generateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + jwtExpiration);

        try {
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .issuedAt(createdDate)
                    .expiration(expirationDate)
                    .signWith(secretKey, Jwts.SIG.HS256)
                    .compact();

            logger.debug("Generated JWT token for user: {}", subject);
            return token;
        } catch (Exception e) {
            logger.error("Could not generate token for user {}: {}", subject, e.getMessage());
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting username from token", e);
        }
    }

    public Date getExpirationFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("Error extracting expiration from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting expiration from token", e);
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            logger.error("Error extracting claims from token: {}", e.getMessage());
            throw new RuntimeException("Error extracting claims from token", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true; // Consider expired if there's an error
        }
    }

    public String generateRefreshToken(User user, UserSession session) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("loginSessionUuid", session.getLoginSessionUuid());
        claims.put("sessionVersion", session.getSessionVersion());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims getRefreshTokenClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            logger.error("Error parsing refresh token: {}", e.getMessage());
            throw new RuntimeException("Invalid refresh token");
        }
    }



}