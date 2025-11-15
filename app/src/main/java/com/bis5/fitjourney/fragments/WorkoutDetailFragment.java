package com.bis5.fitjourney.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bis5.fitjourney.R;
import com.bis5.fitjourney.adapters.ExerciseAdapter;
import com.bis5.fitjourney.adapters.WorkoutHistoryAdapter;
import com.bis5.fitjourney.databinding.FragmentWorkoutDetailBinding;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.WorkoutLog;
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;

public class WorkoutDetailFragment extends Fragment implements WorkoutHistoryAdapter.OnLogClickListener {

    private FragmentWorkoutDetailBinding binding;
    private WorkoutViewModel workoutViewModel;
    private ExerciseAdapter exerciseAdapter;
    private WorkoutHistoryAdapter historyAdapter;
    private String workoutId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            workoutId = WorkoutDetailFragmentArgs.fromBundle(getArguments()).getWorkoutId();
        }

        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.toolbar, navController);

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        WorkoutViewModelFactory viewModelFactory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());
        workoutViewModel = new ViewModelProvider(this, viewModelFactory).get(WorkoutViewModel.class);

        setupRecyclerViews();

        workoutViewModel.getWorkout(workoutId).observe(getViewLifecycleOwner(), workout -> {
            if (workout != null) {
                binding.toolbar.setTitle(workout.getName());
            }
        });

        workoutViewModel.getExercises(workoutId).observe(getViewLifecycleOwner(), exercises -> {
            exerciseAdapter.updateExercises(exercises);
        });

        workoutViewModel.getWorkoutHistory(workoutId).observe(getViewLifecycleOwner(), history -> {
            historyAdapter.updateLogs(history);
        });

        binding.btnAddExercise.setOnClickListener(v -> showAddExerciseDialog());

        binding.btnStartWorkout.setOnClickListener(button -> {
            button.setEnabled(false);
            workoutViewModel.startWorkout(workoutId).observe(getViewLifecycleOwner(), workoutLogId -> {
                if (workoutLogId != null) {
                    WorkoutDetailFragmentDirections.ActionWorkoutDetailFragmentToActiveWorkoutFragment action =
                            WorkoutDetailFragmentDirections.actionWorkoutDetailFragmentToActiveWorkoutFragment(workoutId, workoutLogId);
                    navController.navigate(action);
                }
            });
        });

        binding.btnViewProgress.setOnClickListener(v -> {
            WorkoutDetailFragmentDirections.ActionWorkoutDetailFragmentToProgressFragment action =
                    WorkoutDetailFragmentDirections.actionWorkoutDetailFragmentToProgressFragment(workoutId);
            navController.navigate(action);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.btnStartWorkout.setEnabled(true);
    }

    private void setupRecyclerViews() {
        exerciseAdapter = new ExerciseAdapter();
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvExercises.setAdapter(exerciseAdapter);

        historyAdapter = new WorkoutHistoryAdapter(this);
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(historyAdapter);
    }

    private void showAddExerciseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_exercise, null);

        final EditText etExerciseName = dialogView.findViewById(R.id.etExerciseName);
        final EditText etSets = dialogView.findViewById(R.id.etSets);
        final EditText etReps = dialogView.findViewById(R.id.etReps);
        final EditText etWeight = dialogView.findViewById(R.id.etWeight);

        builder.setView(dialogView)
                .setTitle("Add New Exercise")
                .setPositiveButton("Add", (dialog, id) -> {
                    String name = etExerciseName.getText().toString().trim();
                    String setsStr = etSets.getText().toString();
                    String repsStr = etReps.getText().toString();
                    String weightStr = etWeight.getText().toString();

                    if (!name.isEmpty()) {
                        int sets = setsStr.isEmpty() ? 0 : Integer.parseInt(setsStr);
                        int reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
                        double weight = weightStr.isEmpty() ? 0.0 : Double.parseDouble(weightStr);
                        workoutViewModel.addExercise(workoutId, name, sets, reps, weight);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLogClick(WorkoutLog workoutLog) {
        WorkoutDetailFragmentDirections.ActionWorkoutDetailFragmentToWorkoutSummaryFragment action = 
            WorkoutDetailFragmentDirections.actionWorkoutDetailFragmentToWorkoutSummaryFragment(workoutLog.getId());
        NavHostFragment.findNavController(this).navigate(action);
    }
}
