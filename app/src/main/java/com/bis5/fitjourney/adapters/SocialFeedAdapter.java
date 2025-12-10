package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bis5.fitjourney.R;
import com.bis5.fitjourney.models.SocialPost;

import java.util.Locale;

public class SocialFeedAdapter extends ListAdapter<SocialPost, SocialFeedAdapter.SocialPostViewHolder> {

    private final OnPostInteractionListener listener;

    public interface OnPostInteractionListener {
        void onLikeClick(SocialPost post);
        void onCommentClick(SocialPost post);
    }

    public SocialFeedAdapter(OnPostInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<SocialPost> DIFF_CALLBACK = new DiffUtil.ItemCallback<SocialPost>() {
        @Override
        public boolean areItemsTheSame(@NonNull SocialPost oldItem, @NonNull SocialPost newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SocialPost oldItem, @NonNull SocialPost newItem) {
            return oldItem.getPostContent().equals(newItem.getPostContent()) &&
                   oldItem.getUserName().equals(newItem.getUserName()) &&
                   oldItem.getLikeCount() == newItem.getLikeCount() &&
                   oldItem.getCommentCount() == newItem.getCommentCount();
        }
    };

    @NonNull
    @Override
    public SocialPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_social_post, parent, false);
        return new SocialPostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialPostViewHolder holder, int position) {
        SocialPost currentPost = getItem(position);
        holder.bind(currentPost, listener);
    }

    static class SocialPostViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final TextView tvPostContent;
        private final TextView tvLikeCount;
        private final TextView tvCommentCount;
        private final Button btnLike;
        private final Button btnComment;

        public SocialPostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPostContent = itemView.findViewById(R.id.tvPostContent);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
        }

        public void bind(final SocialPost post, final OnPostInteractionListener listener) {
            tvUserName.setText(post.getUserName());
            tvPostContent.setText(post.getPostContent());
            tvLikeCount.setText(String.format(Locale.getDefault(), "%d Likes", post.getLikeCount()));
            tvCommentCount.setText(String.format(Locale.getDefault(), "%d Comments", post.getCommentCount()));

            btnLike.setOnClickListener(v -> listener.onLikeClick(post));
            btnComment.setOnClickListener(v -> listener.onCommentClick(post));
        }
    }
}
