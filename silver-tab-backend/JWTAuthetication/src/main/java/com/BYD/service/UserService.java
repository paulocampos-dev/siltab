package com.BYD.service;

import com.BYD.dto.user.RegistrationRequest;
import com.BYD.model.User;
import com.BYD.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegistrationRequest request, Long creatorId) {
        try {
            // Validate request
            if (userMapper.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username '" + request.getUsername() + "' already exists");
            }

            // Validate email
            if (userMapper.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setRole(request.getRoleId());
            user.setPositionId(request.getPositionId());
            user.setCreateByUserId(creatorId);

            // Insert user and get the generated ID
            logger.debug("About to insert user: {}", user);
            userMapper.insert(user);

            // After insert, the user object should have the ID populated
            User createdUser = userMapper.findById(user.getId());
            if (createdUser == null) {
                logger.error("User was inserted but could not be found by username");
                throw new RuntimeException("Failed to create user");
            }
            return createdUser;

        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            throw new RuntimeException("Error registering user: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteUser(Long userIdToDelete, Long deleterId) {
        User userToDelete = userMapper.findById(userIdToDelete);
        if (userToDelete == null) {
            throw new RuntimeException("User not found");
        }
        if (userIdToDelete.equals(deleterId)) {
            throw new RuntimeException("Users cannot delete their own accounts");
        }

        User deleter = userMapper.findById(deleterId);
        if (deleter == null) {
            throw new RuntimeException("Deleter not found");
        }

        if (userToDelete.getRole() >= deleter.getRole()) {
            throw new RuntimeException("Cannot delete users with equal or higher role");
        }

        userMapper.deleteById(userIdToDelete);

        logger.info("User {} deleted by user {}", userIdToDelete, deleterId);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    @Transactional
    public User updateUser(Long userId, Map<String, Object> updates) {
        User user = getUserById(userId);

        if (updates.containsKey("username")) {
            String newUsername = (String) updates.get("username");
            if (!user.getUsername().equals(newUsername) && userMapper.existsByUsername(newUsername)) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(newUsername);
        }

        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            if (!user.getEmail().equals(newEmail) && userMapper.existsByEmail(newEmail)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(newEmail);
        }

        if (updates.containsKey("password")) {
            user.setPassword(passwordEncoder.encode((String) updates.get("password")));
        }

        if (updates.containsKey("role")) {
            user.setRole((Integer) updates.get("role"));
        }

        if (updates.containsKey("positionId")) {
            user.setPositionId((Long) updates.get("positionId"));
        }

        user.setLastModifiedDate(LocalDateTime.now());
        userMapper.update(user);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
}