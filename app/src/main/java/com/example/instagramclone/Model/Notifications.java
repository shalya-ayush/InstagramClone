package com.example.instagramclone.Model;

public class Notifications {
    private String userId;
    private String postId;
    private String text;
    private boolean isPost;

    public Notifications() {
    }

    ;

    public Notifications(String userId, String postId, String text, boolean isPost) {
        this.userId = userId;
        this.postId = postId;
        this.text = text;
        this.isPost = isPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
