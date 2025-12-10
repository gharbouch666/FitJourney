package com.bis5.fitjourney.fragments;

import android.graphics.Color;
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

import com.bis5.fitjourney.databinding.FragmentProgressBinding;
import com.bis5.fitjourney.models.AppDatabase;
import com.bis5.fitjourney.models.WorkoutLog;
import com.bis5.fitjourney.viewmodels.WorkoutViewModel;
import com.bis5.fitjourney.viewmodels.WorkoutViewModelFactory;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;
    private WorkoutViewModel workoutViewModel;
    private long workoutId; // THE LAW IS OBEYED

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            // THE REBELLION IS CRUSHED. THE GHOST IS BANISHED.
            workoutId = ProgressFragmentArgs.fromBundle(getArguments()).getWorkoutId();
        }

        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupWithNavController(binding.toolbar, navController);
        binding.toolbar.setTitle("Workout Progress");

        AppDatabase database = AppDatabase.getDatabase(requireContext());
        WorkoutViewModelFactory viewModelFactory = new WorkoutViewModelFactory(database.workoutDao(), database.exerciseDao(), database.workoutLogDao(), database.setLogDao());
        workoutViewModel = new ViewModelProvider(this, viewModelFactory).get(WorkoutViewModel.class);

        setupChart();

        workoutViewModel.getWorkoutHistory(workoutId).observe(getViewLifecycleOwner(), history -> {
            if (history != null && !history.isEmpty()) {
                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < history.size(); i++) {
                    WorkoutLog workoutLog = history.get(i);
                    double volume = 0.0;
                    if (workoutLog.getDateFinished() != null) {
                        volume = (workoutLog.getDateFinished() - workoutLog.getDateStarted()) / 1000.0;
                    }
                    entries.add(new Entry((float) i, (float) volume));
                }

                LineDataSet dataSet = new LineDataSet(entries, "Total Volume Over Time");
                dataSet.setColor(Color.BLUE);
                dataSet.setValueTextColor(Color.BLACK);

                LineData lineData = new LineData(dataSet);
                binding.lineChart.setData(lineData);
                binding.lineChart.invalidate();
            }
        });
    }

    private void setupChart() {
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.lineChart.getAxisRight().setEnabled(false);
        binding.lineChart.setDrawGridBackground(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
