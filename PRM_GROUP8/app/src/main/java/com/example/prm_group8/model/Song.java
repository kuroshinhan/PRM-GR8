package com.example.prm_group8.model;


import java.io.Serializable;

public class Song implements Serializable {
    private int id;
    private String title;
    private String artist;
    private int albumId;
    private int duration;
    private String songUrl; // Thay đổi từ filePath sang songUrl
    private byte[] image;

    // Constructor đầy đủ
    public Song(int id, String title, String artist, int albumId, int duration, String songUrl, byte[] image) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
        this.duration = duration;
        this.songUrl = songUrl;
        this.image = image;
    }

    // Constructor không có id (để tạo đối tượng mới)
    public Song(String title, String artist, int albumId, int duration, String songUrl, byte[] image) {
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
        this.duration = duration;
        this.songUrl = songUrl;
        this.image = image;
    }

    // Getters và Setters
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    // Phương thức chuyển đổi duration từ milliseconds sang format phút:giây
    public String getDurationFormatted() {
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // Phương thức toString() để hiển thị thông tin bài hát
    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", songUrl='" + songUrl + '\'' +
                '}';
    }

    // Phương thức equals() để so sánh hai đối tượng Song
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id;
    }

    // Phương thức hashCode()
    @Override
    public int hashCode() {
        return id;
    }
}