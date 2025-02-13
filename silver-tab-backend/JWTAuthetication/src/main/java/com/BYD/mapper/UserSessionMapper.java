package com.BYD.mapper;
import com.BYD.model.UserSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
@Mapper
@Component
public interface UserSessionMapper {
    // Insert a session
    void insert(UserSession session);
    // update a session
    void update(UserSession session);
    // Delete a session
    void deleteByUserId(@Param("userId") Long userId);
    void deleteByUserIdAndUuid(@Param("userId") Long userId, @Param("loginSessionUuid") String loginSessionUuid);
    // Find a session by User ID and UUID
    UserSession findByUserIdAndUuid(@Param("userId") Long userId, @Param("loginSessionUuid") String loginSessionUuid);
}