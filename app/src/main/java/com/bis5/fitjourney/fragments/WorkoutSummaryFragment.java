package com.bis5.fitjourney.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bis5.fitjourney.adapters.LoggedSetAdapter;
import com.bis5.fitjourney.databinding.FragmentWorkoutSummaryBinding;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.SetLog;
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;

import java.util.List;

public class WorkoutSummaryFragment extends Fragment {

    private FragmentWorkoutSummaryBinding binding;
    private WorkoutViewModel workoutViewModel;
    private LoggedSetAdapter loggedSetAdapter;
    private String workoutLogId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutSummaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            workoutLogId = WorkoutSummaryFragmentArgs.fromBundle(getArguments()).getWorkoutLogId();
        }

        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.toolbar, navController);
        binding.toolbar.setTitle("Workout Summary");

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        WorkoutViewModelFactory viewModelFactory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());
        workoutViewModel = new ViewModelProvider(this, viewModelFactory).get(WorkoutViewModel.class);

        setupRecyclerView();

        workoutViewModel.getLoggedSets(workoutLogId).observe(getViewLifecycleOwner(), loggedSets -> {
            if (loggedSets != null) {
                loggedSetAdapter.updateLoggedSets(loggedSets);

                double totalVolume = 0;
                for (SetLog setLog : loggedSets) {
                    totalVolume += setLog.getWeight() * setLog.getReps();
                }
                binding.tvTotalVolume.setText(String.format("%.1f kg", totalVolume));
            }
        });
    }

    private void setupRecyclerView() {
        loggedSetAdapter = new LoggedSetAdapter();
        binding.rvLoggedSets.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLoggedSets.setAdapter(loggedSetAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
