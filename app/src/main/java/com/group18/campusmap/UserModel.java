package com.group18.campusmap;

public class UserModel {
    String username, email,image;
    boolean isNotificationEnabled;
    //default constructor
    public UserModel() {
    }

    //constructor with arguments


    public UserModel(String username, String email, String image, boolean isNotificationEnabled) {
        this.username = username;
        this.email = email;
        this.image = image;
        this.isNotificationEnabled = isNotificationEnabled;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        isNotificationEnabled = notificationEnabled;
    }
}
