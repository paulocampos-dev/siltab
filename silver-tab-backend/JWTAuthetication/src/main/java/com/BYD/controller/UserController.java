package com.BYD.controller;

import com.BYD.dto.user.RegistrationRequest;
import com.BYD.model.User;
import com.BYD.service.JwtService;
import com.BYD.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RegistrationRequest request) {

        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authentication token"));
            }
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Get creator's role ID directly as Integer
            Integer creatorRoleId = jwtService.getRoleFromToken(token);

            // Check if role >= 3
            if (creatorRoleId == null || creatorRoleId < 3) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to create users"));
            }

            // Get creator's ID from token
            Long creatorId = jwtService.getUserIdFromToken(token);

            // Register new user
            User newUser = userService.registerUser(request, creatorId);

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "userId", newUser.getId(),
                    "username", newUser.getUsername()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Long userId) {

        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authentication token"));
            }
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Get requester's role and ID
            Integer requesterRoleId = jwtService.getRoleFromToken(token);
            Long requesterId = jwtService.getUserIdFromToken(token);

            // Only allow access if user is requesting their own data or has role >= 3 (Moderator or Manager)
            if (!requesterId.equals(userId) && (requesterRoleId == null || requesterRoleId < 3)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to access user data"));
            }

            User user = userService.getUserById(userId);

            // Don't send password in response
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", user.getId());
            userResponse.put("username", user.getUsername());
            userResponse.put("email", user.getEmail());
            userResponse.put("role", user.getRole());
            userResponse.put("createDate", user.getCreateDate());
            userResponse.put("createBy", user.getCreateBy());
            userResponse.put("positionId", user.getPositionId());

            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> updates) {

        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authentication token"));
            }
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Get requester's role and ID
            Integer requesterRoleId = jwtService.getRoleFromToken(token);
            Long requesterId = jwtService.getUserIdFromToken(token);

            // Only allow updates if user is updating their own data or has role >= 3
            if (!requesterId.equals(userId) && (requesterRoleId == null || requesterRoleId < 3)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to update user data"));
            }

            // Additional check: only roles >= 3 can update roles
            if (updates.containsKey("role") && (requesterRoleId == null || requesterRoleId < 3)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to update role"));
            }

            User updatedUser = userService.updateUser(userId, updates);

            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", updatedUser.getId());
            userResponse.put("username", updatedUser.getUsername());
            userResponse.put("email", updatedUser.getEmail());
            userResponse.put("role", updatedUser.getRole());
            userResponse.put("createDate", updatedUser.getCreateDate());
            userResponse.put("createBy", updatedUser.getCreateBy());
            userResponse.put("positionId", updatedUser.getPositionId());

            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("userId") Long userId) {

        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authentication token"));
            }
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Get deleter's ID and role
            Integer deleterRoleId = jwtService.getRoleFromToken(token);
            Long deleterId = jwtService.getUserIdFromToken(token);

            // Only allow deletion by users with role >= 3
            if (deleterRoleId == null || deleterRoleId < 3) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to delete users"));
            }

            userService.deleteUser(userId, deleterId);

            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Validate Bearer token format
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid authentication token"));
            }
            String token = authHeader.substring(7);

            // Validate token
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // Get requester's role from token
            Integer requesterRoleId = jwtService.getRoleFromToken(token);

            // Only allow access to users with role >= 3 (Moderator and Manager)
            if (requesterRoleId == null || requesterRoleId < 3) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to view all users"));
            }

            // Get all users
            List<User> users = userService.getAllUsers();

            // Transform users to DTOs to avoid sending sensitive information
            List<Map<String, Object>> userDTOs = users.stream()
                    .map(user -> {
                        Map<String, Object> userDTO = new HashMap<>();
                        userDTO.put("userid", user.getId());
                        userDTO.put("username", user.getUsername());
                        userDTO.put("email", user.getEmail());
                        userDTO.put("role", user.getRole());
                        userDTO.put("position", user. getPositionId());
                        return userDTO;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(userDTOs);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}