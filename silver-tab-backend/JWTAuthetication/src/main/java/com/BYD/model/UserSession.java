package com.BYD.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER_SESSION")
public class UserSession {
    @Id
    @Column(name = "SESSION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private User user;

    @Column(name = "LOGIN_SESSION_UUID", nullable = false)
    private String loginSessionUuid;

    @Column(name = "SESSION_VERSION", nullable = false)
    private Integer sessionVersion = 1;

    @Column(name = "CREATE_BY")
    private String createBy;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "LAST_MODIFIED_DATE")
    private LocalDateTime lastModifiedDate;

    // Default constructor
    public UserSession() {

    }

    // All getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getLoginSessionUuid() {
        return loginSessionUuid;
    }

    public Integer getSessionVersion() {
        return sessionVersion;
    }

    public String getCreateBy() {
        return createBy;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    // All setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLoginSessionUuid(String loginSessionUuid) {
        this.loginSessionUuid = loginSessionUuid;
    }

    public void setSessionVersion(Integer sessionVersion) {
        this.sessionVersion = sessionVersion;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    //Add toString() method for better logging
    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", loginSessionUuid='" + loginSessionUuid + '\'' +
                ", sessionVersion=" + sessionVersion +
                ", createBy='" + createBy + '\'' +
                ", createDate=" + createDate +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }

}