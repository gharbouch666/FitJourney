package com.bis5.fitjourney.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bis5.fitjourney.R;
import com.bis5.fitjourney.adapters.CommentAdapter;
import com.bis5.fitjourney.adapters.SocialFeedAdapter;
import com.bis5.fitjourney.databinding.FragmentSocialFeedBinding;
import com.bis5.fitjourney.models.Comment;
import com.bis5.fitjourney.models.SocialPost;
import com.bis5.fitjourney.viewmodels.SharedViewModel;
import com.bis5.fitjourney.viewmodels.SocialViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SocialFeedFragment extends Fragment implements SocialFeedAdapter.OnPostInteractionListener {

    private FragmentSocialFeedBinding binding;
    private SocialViewModel socialViewModel;
    private SharedViewModel sharedViewModel;
    private SocialFeedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSocialFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socialViewModel = new ViewModelProvider(this).get(SocialViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setupRecyclerView();

        socialViewModel.getAllPosts().observe(getViewLifecycleOwner(), posts -> {
            if(posts != null) adapter.submitList(posts);
        });

        binding.fabAddPost.setOnClickListener(v -> showAddPostDialog());
    }

    private void setupRecyclerView() {
        adapter = new SocialFeedAdapter(this);
        binding.rvSocialFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSocialFeed.setAdapter(adapter);
    }

    private void showAddPostDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("New Post")
                .setView(R.layout.dialog_add_post) // Assuming a layout for this
                .setPositiveButton("Post", (dialog, which) -> {
                    AlertDialog aDialog = (AlertDialog) dialog;
                    EditText input = aDialog.findViewById(R.id.etPostContent);
                    String content = input.getText().toString();
                    String userName = sharedViewModel.getUserEmail().getValue();
                    if (userName == null || userName.isEmpty()) {
                        userName = "You"; // Fallback
                    }
                    if (!content.isEmpty()) {
                        SocialPost newPost = new SocialPost(userName, content, System.currentTimeMillis(), 0, 0);
                        socialViewModel.insertPost(newPost);
                    } else {
                        Toast.makeText(getContext(), "Post cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onLikeClick(SocialPost post) {
        socialViewModel.likePost(post);
    }

    @Override
    public void onCommentClick(SocialPost post) {
        // ROYAL DECREE: Use MaterialAlertDialogBuilder for a glorious, themed dialog.
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_comments, null);

        final RecyclerView rvComments = dialogView.findViewById(R.id.rvComments);
        final EditText etNewComment = dialogView.findViewById(R.id.etNewComment);
        final Button btnPostComment = dialogView.findViewById(R.id.btnPostComment);

        final CommentAdapter commentAdapter = new CommentAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(commentAdapter);

        socialViewModel.getCommentsForPost(post.getId()).observe(this, comments -> {
            if (comments != null) {
                commentAdapter.submitList(comments);
            }
        });

        builder.setView(dialogView).setTitle("Comments");
        AlertDialog dialog = builder.create();

        btnPostComment.setOnClickListener(v -> {
            String commentText = etNewComment.getText().toString().trim();
            String author = sharedViewModel.getUserEmail().getValue();
             if (author == null || author.isEmpty()) {
                author = "You"; // Fallback
            }

            if (!commentText.isEmpty()) {
                Comment newComment = new Comment(post.getId(), author, commentText, System.currentTimeMillis());
                socialViewModel.addComment(post, newComment);
                etNewComment.setText(""); // Clear input field
            } else {
                Toast.makeText(getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
