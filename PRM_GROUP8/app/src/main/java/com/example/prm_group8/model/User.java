package com.example.prm_group8.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;
    private byte[] image;
    private boolean isEmailVerified;

    public User() {
        this.isEmailVerified = false; // Default value
    }
    // Constructor đầy đủ
    public User(int id, String username, String password, String email, String role, byte[] image, boolean isEmailVerified) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.image = image;
        this.isEmailVerified = isEmailVerified;
    }

    // Constructor không có image và isEmailVerified
    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.isEmailVerified = false; // Default to unverified
    }

    // Constructor không có id và password
    public User(String username, String email, String role, byte[] image) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.image = image;
        this.isEmailVerified = false; // Default to unverified
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", isEmailVerified=" + isEmailVerified +
                '}';
    }
}