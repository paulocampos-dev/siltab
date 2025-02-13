package com.BYD.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


@Mapper
@Component
public interface UserProfileMapper {
    String getRoleNameById(@Param("roleId") Integer roleId);
    String getPositionNameById(@Param("positionId") Long positionId);
    String getUserEntityAuthority(@Param("userId") Long userId, @Param("positionId") Long positionId);
    String checkUserCommercialPolicyAccess(@Param("userId") Long userId, @Param("positionId") Long positionId);
}