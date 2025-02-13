package com.BYD.mapper;
import com.BYD.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import java.util.List;
import java.time.LocalDateTime;
@Mapper
@Component
public interface UserMapper {
    // Find user by username
    User findByUsername(@Param("username") String username);
    // Find user by email
    User findByEmail(@Param("email") String email);
    // Find user by ID
    User findById(@Param("id") Long id);
    // Find user by username and role
    User findByUsernameAndRole(@Param("username") String username, @Param("role") Integer role);
    // Check if username exists
    boolean existsByUsername(@Param("username") String username);
    // Check if email exists
    boolean existsByEmail(@Param("email") String email);
    // Save new user
    void insert(User user);
    // Update existing user
    void update(User user);
    // Delete user by ID
    void deleteById(@Param("id") Long id);
    // Get all users
    List<User> findAll();
    // Update last modified information
    void updateLastModified(@Param("userId") Long userId,
                            @Param("lastModifiedUserId") Long lastModifiedUserId,
                            @Param("lastModifiedDate") LocalDateTime lastModifiedDate);
}