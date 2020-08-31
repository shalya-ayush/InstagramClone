package com.example.instagramclone.Model;

public class Post {
    private String postId;
    private String description;
    private String imageUrl;
    private String author;

    public Post() {
    }   // empty constructor

    public Post(String postId, String description, String imageUrl, String author) {
        this.postId = postId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.author = author;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
