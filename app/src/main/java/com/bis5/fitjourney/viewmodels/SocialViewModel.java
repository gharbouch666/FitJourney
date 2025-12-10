package com.bis5.fitjourney.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.Comment;
import com.bis5.fitjourney.models.CommentDao;
import com.bis5.fitjourney.models.SocialPost;
import com.bis5.fitjourney.models.SocialPostDao;

import java.util.List;

public class SocialViewModel extends AndroidViewModel {

    private final SocialPostDao socialPostDao;
    private final CommentDao commentDao;
    private final LiveData<List<SocialPost>> allPosts;

    public SocialViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        socialPostDao = db.socialPostDao();
        commentDao = db.commentDao();
        allPosts = socialPostDao.getAllPosts();
    }

    // Post-related methods
    public LiveData<List<SocialPost>> getAllPosts() {
        return allPosts;
    }

    public void insertPost(SocialPost post) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            socialPostDao.insert(post);
        });
    }

    public void likePost(SocialPost post) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            post.setLikeCount(post.getLikeCount() + 1);
            socialPostDao.update(post);
        });
    }

    // Comment-related methods
    public LiveData<List<Comment>> getCommentsForPost(int postId) {
        return commentDao.getCommentsForPost(postId);
    }

    public void addComment(SocialPost post, Comment newComment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            commentDao.insert(newComment);
            post.setCommentCount(post.getCommentCount() + 1);
            socialPostDao.update(post);
        });
    }
}
