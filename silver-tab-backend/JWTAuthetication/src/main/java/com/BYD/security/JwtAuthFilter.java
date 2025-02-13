package com.BYD.security;

import com.BYD.service.JwtService;
import com.BYD.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            logger.debug("Processing request to: {}", request.getServletPath());
            String jwt = extractJwtFromRequest(request);

            if (jwt != null) {
                logger.debug("JWT token found in request");
                try {
                    processJwtAuthentication(jwt, request);
                } catch (ExpiredJwtException e) {
                    logger.error("JWT token is expired: {}", e.getMessage());
                    sendUnauthorizedError(response, "JWT token has expired");
                    return;
                } catch (Exception e) {
                    logger.error("JWT processing error: {}", e.getMessage());
                    sendUnauthorizedError(response, "Invalid token");
                    return;
                }
            } else {
                logger.debug("No JWT token found in request to: {}", request.getServletPath());
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", authHeader != null ? "present" : "missing");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }

    private void processJwtAuthentication(String jwt, HttpServletRequest request) {
        if (!jwtService.validateToken(jwt)) {
            logger.warn("Invalid JWT token");
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String username = jwtService.getUsernameFromToken(jwt);
        logger.debug("Token contains username: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateUser(username, request);
        } else {
            logger.debug("Skip authentication: username null or security context not empty");
        }
    }

    private void authenticateUser(String username, HttpServletRequest request) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.debug("Loaded user details for {}, Authorities: {}",
                    username, userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("User authenticated successfully: {} with authorities: {}",
                    username, userDetails.getAuthorities());

        } catch (UsernameNotFoundException e) {
            logger.error("User not found: {}", username);
            throw e;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean shouldNotFilter = path.startsWith("/auth/login");
                //|| path.equals("/stock/allStocks");
        logger.debug("Path: {}, Should not filter: {}", path, shouldNotFilter);
        return shouldNotFilter;
    }
}