package com.BYD.model;

public class UserAccessRegion {
    private Long userId;
    private Integer regionId;

    // Default constructor
    public UserAccessRegion() {
    }

    public UserAccessRegion(Long userId, Integer regionId) {
        this.userId = userId;
        this.regionId = regionId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    @Override
    public String toString() {
        return "UserAccessRegion{" +
                "userId=" + userId +
                ", regionId=" + regionId +
                '}';
    }
}