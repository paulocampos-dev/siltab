package com.BYD.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER_ACCESS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ROLE_ID")
    private Integer role;

    @Column(name = "POSITION_ID")
    private Long positionId;

    @Column(name = "CREATE_BY")
    private String createBy;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "CREATE_BY_USER_ID")
    private Long createByUserId;

    @Column(name = "LAST_MODIFIED_USER_ID")
    private Long lastModifiedUserId;

    @Column(name = "LAST_MODIFIED_DATE")
    private LocalDateTime lastModifiedDate;

    // Default constructor required by JPA
    public User() {
    }
    // Updated constructor with all relevant fields
    public User(Long id, String username, String password, String email,
                Integer role, Long positionId, String createBy,
                LocalDateTime createDate, Long createByUserId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.positionId = positionId;
        this.createBy = createBy;
        this.createDate = createDate;
        this.createByUserId = createByUserId;
    }

    // Getters
    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Integer getRole() { return role; }

    public Long getPositionId() { return positionId; }

    public String getCreateBy() {
        return createBy;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public Long getCreateByUserId() { return createByUserId; }

    public Long getLastModifiedUserId() { return lastModifiedUserId; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public void setPositionId(Long positionId) { this.positionId = positionId; }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public void setCreateByUserId(Long createByUserId) { this.createByUserId = createByUserId; }

    public void setLastModifiedUserId(Long lastModifiedUserId) { this.lastModifiedUserId = lastModifiedUserId; }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", positionId=" + positionId +
                ", createBy='" + createBy + '\'' +
                ", createDate=" + createDate +
                ", createByUserId=" + createByUserId +
                '}';
    }
}