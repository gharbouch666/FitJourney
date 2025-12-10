package com.bis5.fitjourney.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "comments",
        foreignKeys = @ForeignKey(entity = SocialPost.class,
                                  parentColumns = "id",
                                  childColumns = "postId",
                                  onDelete = ForeignKey.CASCADE))
public class Comment {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int postId;
    private String authorName;
    private String content;
    private long timestamp;

    // Constructor
    public Comment(int postId, String authorName, String content, long timestamp) {
        this.postId = postId;
        this.authorName = authorName;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }
}
