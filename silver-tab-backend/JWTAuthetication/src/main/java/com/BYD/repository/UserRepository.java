//package com.BYD.repository;
//
//import com.BYD.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//
//
//    // Find user by username
//    @Query("SELECT u FROM User u WHERE u.username = :username")
//    Optional<User> findByUsername(@Param("username") String username);
//
//    // Check if username exists
//    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
//    boolean existsByUsername(@Param("username") String username);
//
//    // Find user by email
//    @Query("SELECT u FROM User u WHERE u.email = :email")
//    Optional<User> findByEmail(@Param("email") String email);
//
//    // Check if exist an user with this email
//    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
//    boolean existsByEmail(@Param("email") String email);
//
//    // Find user by username and role
//    @Query("SELECT u FROM User u WHERE u.username = :username AND u.role = :role")
//    Optional<User> findByUsernameAndRole(@Param("username") String username, @Param("role") Integer role);
//}