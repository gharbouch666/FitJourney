package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.databinding.ItemWorkoutBinding;
import com.bis5.fitjourney.models.Workout;

import java.util.Objects;

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

            int iconRes;
            switch (workout.getName().toLowerCase()) {
                case "push day":
                    iconRes = R.drawable.push;
                    break;
                case "pull day":
                    iconRes = R.drawable.pull;
                    break;
                case "leg day":
                    iconRes = R.drawable.legs;
                    break;
                case "upper body":
                    iconRes = R.drawable.upper;
                    break;
                case "lower body":
                    iconRes = R.drawable.lower;
                    break;
                case "core":
                    iconRes = R.drawable.core;
                    break;
                case "cardio":
                    iconRes = R.drawable.cardio;
                    break;
                default:
                    iconRes = R.drawable.dumbell;
                    break;
            }
            binding.ivWorkoutIcon.setImageResource(iconRes);
        }
    }

    private static final DiffUtil.ItemCallback<Workout> WORKOUT_DIFF_CALLBACK = new DiffUtil.ItemCallback<Workout>() {
        @Override
        public boolean areItemsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
            return oldItem.equals(newItem);
        }
    };
}
