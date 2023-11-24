package com.ppl.stumanage.UserManagement;

public class LoginHistoryModel {
    private String username;
    private String email;
    private String loginTime;

    public LoginHistoryModel(String username, String email, String loginTime) {
        this.username = username;
        this.email = email;
        this.loginTime = loginTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

}