package com.BYD.dto.user;

import com.BYD.model.User;

public class UserTokenResponse {
    private final String token;
    private final User user;

    public UserTokenResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}