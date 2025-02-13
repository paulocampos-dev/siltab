package com.BYD.config;

import com.BYD.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Define security rules with minimum role levels
        Map<String, Integer> securityRules = new HashMap<>();
        securityRules.put("/auth/**", 0);          // Public access for login, refresh token, validate
        securityRules.put("/users/register/**", 3);  // Minimum role 3 (MODERATOR)
        securityRules.put("/users/{userId}:GET", 1);    // Any authenticated user can view (role >= 1)
        securityRules.put("/users/{userId}:PATCH", 3);  // Only moderator (3) and above can update
        securityRules.put("/users/{userId}:PUT", 3);    // Only moderator (3) and above can update
        securityRules.put("/users/{userId}:DELETE", 3); // Only moderator (3) and above can delete
        securityRules.put("/users:GET", 3); // Only moderator (3) and above can delete
        securityRules.put("/stock/allStocks", 0);   // Public access


        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    // Configure each path with its minimum role requirement
                    securityRules.forEach((path, minRole) -> {
                        if (minRole == 0) {
                            auth.requestMatchers(path).permitAll();
                        } else {
                            auth.requestMatchers(path).access((authenticationContext, object) -> {
                                Authentication authentication = authenticationContext.get();
                                // Get user's role number from authorities
                                int userRole = authentication.getAuthorities().stream()
                                        .map(a -> Integer.parseInt(a.getAuthority()))
                                        .findFirst()
                                        .orElse(0);

                                // Simple numeric comparison
                                return new AuthorizationDecision(userRole >= minRole);
                            });
                        }
                    });
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://bgate.bydauto.com",
                "https://bgate-uat.bydauto.com",
                "http://localhost:5000",
                "http://localhost:5001",
                "http://localhost:5173",
                "http://localhost:4173",
                "http://localhost:80",
                "http://localhost",
                "http://10.42.253.88:5173",
                "http://10.42.253.88:5000",
                "http://10.42.253.85:5173",
                "http://10.42.253.85:5000",
                "http://10.42.253.90:5173",
                "http://10.42.253.90:5000")); // Your frontend URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}