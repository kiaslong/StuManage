package com.ppl.stumanage.UserManagement;

public class SystemUser {
    private String userName;
    private int userAge;
    private String userPhoneNumber;
    private String userEmail;
    private String userStatus;

    public SystemUser(String userName, int userAge, String userPhoneNumber, String userEmail, String userStatus) {
        this.userName = userName;
        this.userAge = userAge;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
        this.userStatus = userStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
