package com.example.prm_group8.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String phoneNumber;
    private String role;
    private byte[] image; // Thay đổi kiểu dữ liệu từ String sang byte[]

    // Constructor đầy đủ
    public User(int id, String username, String password, String phoneNumber, String role, byte[] image) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.image = image;
    }

    // Constructor không có image
    public User(String username, String phoneNumber, String role) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Constructor không có id và password
    public User(String username, String phoneNumber, String role, byte[] image) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.image = image;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

}