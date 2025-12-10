package com.bis5.fitjourney.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ActiveExerciseAdapter extends RecyclerView.Adapter<ActiveExerciseAdapter.ActiveExerciseViewHolder> {

    public interface OnLogSetListener {
        void onLogSet(String exerciseName, int setNumber, double weight, int reps);
    }

    private List<Exercise> exercises = new ArrayList<>();
    private final OnLogSetListener onLogSetListener;

    public ActiveExerciseAdapter(OnLogSetListener listener) {
        this.onLogSetListener = listener;
    }

    @NonNull
    @Override
    public ActiveExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_exercise, parent, false);
        return new ActiveExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());

        // The ActiveSetAdapter is responsible for the sets of EACH exercise.
        ActiveSetAdapter setAdapter = new ActiveSetAdapter(exercise, (setNumber, weight, reps) -> {
            if (onLogSetListener != null) {
                onLogSetListener.onLogSet(exercise.getName(), setNumber, weight, reps);
            }
        });
        holder.setsRecyclerView.setAdapter(setAdapter);
    }

    @Override
    public int getItemCount() {
        return exercises == null ? 0 : exercises.size();
    }

    public void updateExercises(List<Exercise> newExercises) {
        this.exercises = newExercises;
        notifyDataSetChanged();
    }


    public static class ActiveExerciseViewHolder extends RecyclerView.ViewHolder {
        final TextView exerciseName;
        final RecyclerView setsRecyclerView;

        public ActiveExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.tvExerciseName);
            setsRecyclerView = itemView.findViewById(R.id.rvSets);

            // THIS WAS THE BUG. THE RECYCLERVIEW FOR THE SETS WAS MISSING ITS LAYOUTMANAGER.
            // I HAVE NOW FIXED IT.
            setsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
