package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.ItemWorkoutBinding;
import com.bis5.fitjourney.models.Workout;

public class WorkoutListAdapter extends ListAdapter<Workout, WorkoutListAdapter.WorkoutViewHolder> {

    public interface OnWorkoutListener {
        void onItemClicked(Workout workout);
        void onLongItemClicked(Workout workout);
    }

    private final OnWorkoutListener listener;

    public WorkoutListAdapter(@NonNull OnWorkoutListener listener) {
        super(WORKOUT_DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkoutBinding binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkoutViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout currentWorkout = getItem(position);
        holder.bind(currentWorkout);
        holder.itemView.setOnClickListener(v -> listener.onItemClicked(currentWorkout));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongItemClicked(currentWorkout);
            return true;
        });
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final ItemWorkoutBinding binding;

        public WorkoutViewHolder(ItemWorkoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Workout workout) {
            binding.tvWorkoutName.setText(workout.getName());

            // Set icon based on workout name
            int iconRes = getIconForWorkout(workout.getName());
            binding.ivWorkoutIcon.setImageResource(iconRes);

            // For now, these fields are hidden as they are not in the Workout object
            binding.tvExerciseCount.setVisibility(View.GONE);
            binding.tvLastPerformed.setVisibility(View.GONE);
        }

        private int getIconForWorkout(String workoutName) {
            if (workoutName == null) return R.drawable.ic_fitness;
            String lowerCaseName = workoutName.toLowerCase();

            if (lowerCaseName.contains("push")) {
                return R.drawable.push;
            } else if (lowerCaseName.contains("pull")) {
                return R.drawable.pull;
            } else if (lowerCaseName.contains("leg")) {
                return R.drawable.legs;
            } else if (lowerCaseName.contains("core")) {
                return R.drawable.core;
            } else if (lowerCaseName.contains("cardio")) {
                return R.drawable.cardio;
            } else if (lowerCaseName.contains("upper")) {
                return R.drawable.upper;
            } else if (lowerCaseName.contains("lower")) {
                return R.drawable.lower;
            } else {
                return R.drawable.ic_fitness;
            }
        }
    }

    private static final DiffUtil.ItemCallback<Workout> WORKOUT_DIFF_CALLBACK = new DiffUtil.ItemCallback<Workout>() {
        @Override
        public boolean areItemsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
            // THE FINAL PURGE: The treasonous code is CRUSHED.
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
            return oldItem.equals(newItem);
        }
    };
}
