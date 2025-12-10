package com.bis5.fitjourney.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SocialPostDao {

    @Insert
    long insert(SocialPost post);

    @Update
    void update(SocialPost post);

    @Query("SELECT * FROM social_posts ORDER BY timestamp DESC")
    LiveData<List<SocialPost>> getAllPosts();

    @Query("UPDATE social_posts SET commentCount = commentCount + 1 WHERE id = :postId")
    void incrementCommentCount(int postId);

    // COMMAND CENTER: A new decree to get the latest post's content.
    @Query("SELECT postContent FROM social_posts ORDER BY timestamp DESC LIMIT 1")
    LiveData<String> getLatestPostContent();
}
