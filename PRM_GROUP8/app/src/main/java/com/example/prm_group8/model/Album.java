package com.example.prm_group8.model;


import java.io.Serializable;

public class Album implements Serializable {
    private int id;
    private String title;
    private byte[] image;
    private String releaseDate;
    private int userId;

    // Constructor đầy đủ
    public Album(int id, String title, byte[] image, String releaseDate, int userId) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.releaseDate = releaseDate;
        this.userId = userId;
    }

    // Constructor không có id (có thể sử dụng khi tạo album mới)
    public Album(String title, byte[] image, String releaseDate, int userId) {
        this.title = title;
        this.image = image;
        this.releaseDate = releaseDate;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", userId=" + userId +
                '}';
    }
}
