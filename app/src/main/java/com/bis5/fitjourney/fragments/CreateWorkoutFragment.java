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
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;
import com.google.android.material.chip.Chip;

public class CreateWorkoutFragment extends Fragment {

    private FragmentCreateWorkoutBinding binding;
    private WorkoutViewModel workoutViewModel;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateWorkoutBinding.inflate(inflater, container, false);
        if (requireActivity().getIntent() != null) {
            userEmail = requireActivity().getIntent().getStringExtra("USER_EMAIL");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        WorkoutViewModelFactory viewModelFactory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());
        workoutViewModel = new ViewModelProvider(this, viewModelFactory).get(WorkoutViewModel.class);

        binding.btnSaveWorkout.setOnClickListener(v -> saveWorkout());
    }

    private void saveWorkout() {
        int selectedChipId = binding.cgWorkoutTypes.getCheckedChipId();
        String customName = binding.etWorkoutName.getText().toString().trim();
        String workoutName = "";

        if (selectedChipId != View.NO_ID) {
            Chip selectedChip = getView().findViewById(selectedChipId);
            if(selectedChip != null) {
               workoutName = selectedChip.getText().toString();
            }
        } else {
            workoutName = customName;
        }

        if (workoutName.isEmpty()) {
            Toast.makeText(requireContext(), "Please select or enter a workout name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userEmail != null) {
            workoutViewModel.createWorkout(workoutName, userEmail);
            Toast.makeText(requireContext(), "Workout '" + workoutName + "' saved!", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
