//package com.BYD.repository;
//
//import com.BYD.model.UserSession;
//import com.BYD.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.util.Optional;
//import java.util.List;
//
//
//@Repository
//public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
//    Optional<UserSession> findByUserAndLoginSessionUuid(User user, String loginSessionUuid);
//    void deleteByUserAndLoginSessionUuid(User user, String loginSessionUuid);
//    List<UserSession> findByUser(User user);
//}