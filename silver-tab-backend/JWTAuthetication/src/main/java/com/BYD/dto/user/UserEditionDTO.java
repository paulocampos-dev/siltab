package com.BYD.dto.user;

public class UserEditionDTO {
    private String username;
    private String email;
    private Integer roleId;
    private Long positionId;
    private String password; // Optional - only if changing password

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    public Long getPositionId() { return positionId; }
    public void setPositionId(Long positionId) { this.positionId = positionId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}