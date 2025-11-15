package com.bis5.fitjourney.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bis5.fitjourney.adapters.WorkoutListAdapter;
import com.bis5.fitjourney.databinding.FragmentWorkoutListBinding;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.Workout;
import com.bis5.fitjourney.viewmodels.SharedViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;

public class WorkoutListFragment extends Fragment implements WorkoutListAdapter.OnWorkoutListener {

    private FragmentWorkoutListBinding binding;
    private WorkoutViewModel workoutViewModel;
    private SharedViewModel sharedViewModel;
    private WorkoutListAdapter workoutListAdapter;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the SharedViewModel with the Activity's scope
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        WorkoutViewModelFactory viewModelFactory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());
        workoutViewModel = new ViewModelProvider(this, viewModelFactory).get(WorkoutViewModel.class);

        setupRecyclerView();

        // Observe the user's email from the SharedViewModel
        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null && !email.isEmpty()) {
                this.userEmail = email;
                // Once we have the email, observe the workouts
                workoutViewModel.getWorkouts(userEmail).observe(getViewLifecycleOwner(), workouts -> {
                    workoutListAdapter.submitList(workouts);
                });
            }
        });

        binding.fabAddWorkout.setOnClickListener(v -> {
            NavDirections action = 
                WorkoutListFragmentDirections.actionWorkoutListFragmentToCreateWorkoutFragment();
            NavHostFragment.findNavController(this).navigate(action);
        });
    }

    private void setupRecyclerView() {
        workoutListAdapter = new WorkoutListAdapter(this);
        binding.rvWorkouts.setAdapter(workoutListAdapter);
        binding.rvWorkouts.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void showEditDeleteDialog(Workout workout) {
        final CharSequence[] options = {"Edit", "Delete"};
        new AlertDialog.Builder(requireContext())
            .setTitle("Choose Action")
            .setItems(options, (dialog, which) -> {
                if (which == 0) {
                    showEditWorkoutDialog(workout);
                } else if (which == 1) {
                    showDeleteConfirmationDialog(workout);
                }
            })
            .show();
    }

    private void showEditWorkoutDialog(Workout workout) {
        final EditText editText = new EditText(requireContext());
        editText.setText(workout.getName());

        new AlertDialog.Builder(requireContext())
            .setTitle("Edit Workout Name")
            .setView(editText)
            .setPositiveButton("Save", (dialog, which) -> {
                String newName = editText.getText().toString().trim();
                if (!newName.isEmpty()) {
                    workoutViewModel.updateWorkoutName(workout.getId(), newName);
                    Toast.makeText(requireContext(), "Workout updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showDeleteConfirmationDialog(Workout workout) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Workout")
            .setMessage("Are you sure you want to delete '" + workout.getName() + "'?")
            .setPositiveButton("Delete", (dialog, which) -> {
                workoutViewModel.deleteWorkout(workout);
                Toast.makeText(requireContext(), "Workout deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClicked(Workout workout) {
        NavDirections action = 
            WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutDetailFragment(workout.getId());
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onLongItemClicked(Workout workout) {
        showEditDeleteDialog(workout);
    }
}
