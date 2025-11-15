package com.bis5.fitjourney.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bis5.fitjourney.adapters.ActiveExerciseAdapter;
import com.bis5.fitjourney.databinding.FragmentActiveWorkoutBinding;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;

public class ActiveWorkoutFragment extends Fragment implements ActiveExerciseAdapter.OnLogSetListener {

    private FragmentActiveWorkoutBinding binding;
    private WorkoutViewModel workoutViewModel;
    private ActiveExerciseAdapter exerciseAdapter;
    private String workoutId;
    private String workoutLogId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActiveWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            workoutId = ActiveWorkoutFragmentArgs.fromBundle(getArguments()).getWorkoutId();
            workoutLogId = ActiveWorkoutFragmentArgs.fromBundle(getArguments()).getWorkoutLogId();
        }

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        WorkoutViewModelFactory viewModelFactory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());
        workoutViewModel = new ViewModelProvider(this, viewModelFactory).get(WorkoutViewModel.class);

        setupRecyclerView();

        workoutViewModel.getExercises(workoutId).observe(getViewLifecycleOwner(), exercises -> {
            exerciseAdapter.updateExercises(exercises);
        });

        workoutViewModel.getWorkout(workoutId).observe(getViewLifecycleOwner(), workout -> {
            if (workout != null) {
                binding.toolbar.setTitle(workout.getName());
            }
        });

        binding.btnFinishWorkout.setOnClickListener(v -> {
            workoutViewModel.finishWorkout(workoutLogId);
            Toast.makeText(getContext(), "Workout Finished!", Toast.LENGTH_SHORT).show();
            NavController navController = NavHostFragment.findNavController(this);
            ActiveWorkoutFragmentDirections.ActionActiveWorkoutFragmentToWorkoutSummaryFragment action =
                    ActiveWorkoutFragmentDirections.actionActiveWorkoutFragmentToWorkoutSummaryFragment(workoutLogId);
            navController.navigate(action);
        });
    }

    private void setupRecyclerView() {
        exerciseAdapter = new ActiveExerciseAdapter(this);
        binding.rvActiveExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvActiveExercises.setAdapter(exerciseAdapter);
    }

    @Override
    public void onLogSet(String exerciseName, int setNumber, double weight, int reps) {
        workoutViewModel.logSet(workoutLogId, exerciseName, setNumber, reps, weight);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
