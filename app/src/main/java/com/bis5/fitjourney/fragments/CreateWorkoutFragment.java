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
import androidx.navigation.fragment.NavHostFragment;

import com.bis5.fitjourney.databinding.FragmentCreateWorkoutBinding;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.viewmodels.SharedViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;
import com.google.android.material.chip.Chip;

public class CreateWorkoutFragment extends Fragment {

    private FragmentCreateWorkoutBinding binding;
    private WorkoutViewModel workoutViewModel;
    private SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        WorkoutViewModelFactory factory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());

        // *** THE FIX: Scope the ViewModel to the Activity to share it across all fragments. ***
        workoutViewModel = new ViewModelProvider(requireActivity(), factory).get(WorkoutViewModel.class);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnBackToWorkouts.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        binding.cgWorkoutTypes.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip selectedChip = group.findViewById(checkedId);
                if (selectedChip != null) {
                    binding.etWorkoutName.setText(selectedChip.getText().toString());
                }
            }
        });

        binding.btnSaveWorkout.setOnClickListener(v -> {
            String workoutName = binding.etWorkoutName.getText().toString().trim();
            String userEmail = sharedViewModel.getUserEmail().getValue();

            if (workoutName.isEmpty()) {
                Toast.makeText(getContext(), "Please select or enter a workout name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userEmail == null || userEmail.isEmpty()) {
                 Toast.makeText(getContext(), "Error: User data not available. Please restart the app.", Toast.LENGTH_LONG).show();
                return;
            }

            workoutViewModel.createWorkout(workoutName, userEmail);
            Toast.makeText(getContext(), "'" + workoutName + "' created!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
