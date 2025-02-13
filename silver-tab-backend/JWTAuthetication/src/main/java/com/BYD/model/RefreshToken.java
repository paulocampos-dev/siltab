package com.BYD.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "REFRESH_TOKEN")
public class RefreshToken {
    @Id
    @Column(name = "TOKEN_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TOKEN", nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private User user;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private Instant expiryDate;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Optional: Add toString() method for logging
    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", user=" + user.getUsername() +  // Only include username for security
                ", expiryDate=" + expiryDate +
                '}';
    }
}