package com.example.instagramclone.Model;
// User class to show user in the search fragment
public class User {
    private String username;
    private String fullName;
    private String email;
    private String imageUrl;
    private String bio;
    private String id;

    public User() {

    }

    public User(String username, String fullName, String email, String imageUrl, String bio, String id) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
