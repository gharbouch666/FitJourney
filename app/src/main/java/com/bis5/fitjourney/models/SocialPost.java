package com.bis5.fitjourney.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "social_posts")
public class SocialPost {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userName;
    private String postContent;
    private long timestamp;
    private int likeCount;
    private int commentCount;

    // Constructor
    public SocialPost(String userName, String postContent, long timestamp, int likeCount, int commentCount) {
        this.userName = userName;
        this.postContent = postContent;
        this.timestamp = timestamp;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPostContent() {
        return postContent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
