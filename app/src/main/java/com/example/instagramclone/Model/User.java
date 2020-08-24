package com.example.instagramclone.Model;

public class User {
    private String username;
    private String fullname;
    private String email;
    private String id;

    public User() {

    }

    public User(String username, String fullname, String email, String id) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
