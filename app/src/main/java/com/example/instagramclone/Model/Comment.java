package com.example.instagramclone.Model;

public class Comment {
    private String id;
    private String author;
    private String comment;

    public Comment() {

    }

    public Comment(String id, String author, String comment) {
        this.id = id;
        this.author = author;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
