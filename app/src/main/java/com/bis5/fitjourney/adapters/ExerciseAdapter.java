package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises = new ArrayList<>();

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.sets.setText("Sets: " + exercise.getSets());
        holder.reps.setText("Reps: " + exercise.getReps());
        holder.weight.setText("Weight: " + exercise.getWeight() + " kg");
    }

    @Override
    public int getItemCount() {
        return exercises == null ? 0 : exercises.size();
    }

    public void updateExercises(List<Exercise> newExercises) {
        this.exercises = newExercises;
        notifyDataSetChanged(); // This is inefficient, but matches the original code.
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        final TextView exerciseName;
        final TextView sets;
        final TextView reps;
        final TextView weight;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.tvExerciseName);
            sets = itemView.findViewById(R.id.tvSets);
            reps = itemView.findViewById(R.id.tvReps);
            weight = itemView.findViewById(R.id.tvWeight);
        }
    }
}
